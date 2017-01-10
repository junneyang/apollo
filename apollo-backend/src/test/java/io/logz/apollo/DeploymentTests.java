package io.logz.apollo;

import io.logz.apollo.clients.ApolloClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 1/5/17.
 */
public class DeploymentTests {

    @Test
    public void testGetAndAddDeployment() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment testDeployment = createAndSumbitDeployment(apolloTestClient);

        Deployment returnedDeployment = apolloTestClient.getDeployment(testDeployment.getId());

        assertThat(returnedDeployment.getEnvironmentId()).isEqualTo(testDeployment.getEnvironmentId());
        assertThat(returnedDeployment.getServiceId()).isEqualTo(testDeployment.getServiceId());
        assertThat(returnedDeployment.getDeployableVersionId()).isEqualTo(testDeployment.getDeployableVersionId());
        assertThat(returnedDeployment.getUserEmail()).isEqualTo(testDeployment.getUserEmail());
        assertThat(returnedDeployment.getStatus()).isEqualTo(testDeployment.getStatus());
        assertThat(returnedDeployment.getSourceVersion()).isEqualTo(testDeployment.getSourceVersion());

    }

    @Test
    public void testGetAllDeployments() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment testDeployment = createAndSumbitDeployment(apolloTestClient);

        Optional<Deployment> deploymentFromApi = apolloTestClient.getAllDeployments().stream()
                .filter(deployment -> deployment.getId() == testDeployment.getId()).findFirst();

        boolean found = false;

        if (deploymentFromApi.isPresent()) {
            if (deploymentFromApi.get().getEnvironmentId() == testDeployment.getEnvironmentId() &&
                    deploymentFromApi.get().getServiceId() == testDeployment.getServiceId() &&
                    deploymentFromApi.get().getDeployableVersionId() == testDeployment.getDeployableVersionId() &&
                    deploymentFromApi.get().getStatus().toString().equals(testDeployment.getStatus().toString()) &&
                    deploymentFromApi.get().getSourceVersion().equals(testDeployment.getSourceVersion())) {
                found = true;
            }
        }

        assertThat(found).isTrue();
    }

    private Deployment createAndSumbitDeployment(ApolloTestClient apolloTestClient) throws ApolloClientException {

        // Add all foreign keys
        Environment testEnvironment = ModelsGenerator.createEnvironment();
        testEnvironment.setId(apolloTestClient.addEnvironment(testEnvironment).getId());

        Service testService = ModelsGenerator.createService();
        testService.setId(apolloTestClient.addService(testService).getId());

        DeployableVersion testDeployableVersion = ModelsGenerator.createDeployableVersion(testService);
        testDeployableVersion.setId(apolloTestClient.addDeployableVersion(testDeployableVersion).getId());

        // Now we have enough to create a deployment
        Deployment testDeployment = ModelsGenerator.createDeployment(testService, testEnvironment, testDeployableVersion, apolloTestClient.getClientUser());
        testDeployment.setId(apolloTestClient.addDeployment(testDeployment).getId());

        return testDeployment;
    }
}

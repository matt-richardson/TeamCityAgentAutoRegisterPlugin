package agentAutoAuthorize.server;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.agentPools.AgentPool;
import jetbrains.buildServer.serverSide.agentPools.AgentPoolManager;
import jetbrains.buildServer.serverSide.agentPools.NoSuchAgentPoolException;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;


public class ServerListener extends BuildServerAdapter {
  final static Logger LOG = Logger.getInstance(ServerListener.class.getName());

  protected static final String PLUGIN_NAME = "tokenAgentAuthorize";
  protected static final String AUTHORIZATION_TOKEN_NAME = "teamcity.agentAutoAuthorize.authorizationToken";
  @NotNull
  private final BuildAgentManager myAgentManager;
  @NotNull
  private final AgentPoolManager myAgentPoolManager;
  @NotNull
  private final TokenStore myTokenStore;
  @NotNull
  private SBuildServer myServer;

  public ServerListener(@NotNull final EventDispatcher<BuildServerListener> dispatcher,
                        @NotNull SBuildServer server,
                        @NotNull BuildAgentManager agentManager,
                        @NotNull AgentPoolManager agentPoolManager,
                        @NotNull TokenStore tokenStore) {
    this.myAgentManager = agentManager;
    myAgentPoolManager = agentPoolManager;
    myTokenStore = tokenStore;
    dispatcher.addListener(this);
    myServer = server;
  }

  @Override
  public void serverStartup() {
    Loggers.SERVER.info("Plugin '" + PLUGIN_NAME + "'. Is running on server version " + myServer.getFullServerVersion() + ".");
  }

  @Override
  public void agentRegistered(@NotNull SBuildAgent agent, long currentlyRunningBuildId) {
    if (agent.isAuthorized()){
      return;
    }

    final String authToken = getTokenForAgent(agent);
    if (authToken == null) {
      LOG.debug("Found no \"" + AUTHORIZATION_TOKEN_NAME + "\" config parameter for agent " + agent.describe(false));
      return;
    }

    final TokenStore.TokenData data = myTokenStore.getData(authToken);
    if (data == null) {
      LOG.debug("Found no defined token data for token \"" + authToken + "\" for agent " + agent.describe(false));
      return;
    }
    final String agentPoolId = data.getAgentPoolId();
    if (agentPoolId != null){
      final Integer parsedPoolId = Integer.valueOf(agentPoolId); //todo handle exceptions
      final AgentPool agentPool = myAgentPoolManager.findAgentPoolById(parsedPoolId);
      if (agentPool != null){
        try {
          myAgentPoolManager.moveAgentTypesToPool(parsedPoolId, Collections.singleton(agent.getAgentTypeId()));
        } catch (NoSuchAgentPoolException e) {
          LOG.error("Error assigning an agent to pool " + e.toString()); //todo
        }
      }
    }
    agent.setAuthorized(true, null, "Authorized by agent token authorize plugin, token \"" + data.getName() + "\"");
    setAgentPrameter(agent, AUTHORIZATION_TOKEN_NAME, getSubstitutionValue(data));

    // handle "cannot be authorized because there is not enough licenses."
    //filter from agent...
    //filter from build: jetbrains.buildServer.parameters.PasswordParametersFilterCore#VALUES_LIST_CONFIG_PARAMETER_NAME

    // do not authorize on manual unauthorize with comment
  }

  private void setAgentPrameter(SBuildAgent agent, String parameterName, String newParameterValue) {
    agent.getConfigurationParameters().put(parameterName, newParameterValue); //todo here exception is thrown at this time
  }

  private String getSubstitutionValue(TokenStore.TokenData data) {
    return "processed token \"" + data.getName() + "\"";
  }

  private String getTokenForAgent(SBuildAgent agent) {
    return agent.getConfigurationParameters().get(AUTHORIZATION_TOKEN_NAME);
  }
}

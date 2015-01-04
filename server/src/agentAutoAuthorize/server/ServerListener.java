package agentAutoAuthorize.server;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.agentPools.AgentPoolManager;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;


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

    final boolean isValid = myTokenStore.isValid(authToken);
    if (isValid) {
      agent.setAuthorized(true, null, "Authorized by agent token authorize plugin");
      setAgentParameter(agent, AUTHORIZATION_TOKEN_NAME, authToken);
    }
    // handle "cannot be authorized because there is not enough licenses."
    //filter from agent...
    //filter from build: jetbrains.buildServer.parameters.PasswordParametersFilterCore#VALUES_LIST_CONFIG_PARAMETER_NAME

    // do not authorize on manual unauthorize with comment
  }

  private void setAgentParameter(SBuildAgent agent, String parameterName, String newParameterValue) {
    agent.getConfigurationParameters().put(parameterName, newParameterValue); //todo here exception is thrown at this time
  }

  private String getTokenForAgent(SBuildAgent agent) {
    return agent.getConfigurationParameters().get(AUTHORIZATION_TOKEN_NAME);
  }
}

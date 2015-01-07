// original licence from agentMagicAuthorize plugin - https://confluence.jetbrains.com/display/TW/Agent+Custom+Token+Authorize+Plugin
/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * additional changes by Matt Richardson - Jan 2015
 * additional changes licenced under Apache License, Version 2.0 as above
 */

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
    }
    // handle "cannot be authorized because there is not enough licenses."
    // do not authorize on manual unauthorize with comment
  }

  private String getTokenForAgent(SBuildAgent agent) {
    return agent.getConfigurationParameters().get(AUTHORIZATION_TOKEN_NAME);
  }
}

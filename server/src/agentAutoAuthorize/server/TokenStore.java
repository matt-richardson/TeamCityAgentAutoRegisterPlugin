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

package agentAutoAuthorize.server;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.TeamCityProperties;

public class TokenStore {
  final static Logger LOG = Logger.getInstance(ServerListener.class.getName());

  public boolean isValid(String token) {
    final String clientToken = token.trim();
    final String serverToken = TeamCityProperties.getProperty("teamcity.agentAutoAuthorize.authorizationToken").trim();
    if (serverToken == null) {
      LOG.debug("Found no defined server side token - set property 'teamcity.agentAutoAuthorize.authorizationToken' in internal.properties");
      return false;
    }
    return serverToken.equals(clientToken);
  }
}

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

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.filters.Filter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Yegor.Yarko
 *         Date: 10.04.2014
 */
public class TokenStore {
  private final List<String> myUsedTokens = new ArrayList<String>();

  public TokenData getData(String token) {
    final String trimmedToken = token.trim();
    return CollectionsUtil.findFirst(getAllTokenData(), new Filter<TokenData>() {
      public boolean accept(@NotNull TokenData data) {
        return data.getToken().equals(trimmedToken);
      }
    });
  }

  private List<TokenData> getAllTokenData() {
    final ArrayList<TokenData> result = new ArrayList<TokenData>();

    final String rawTokens = TeamCityProperties.getProperty("agent.authorize.tokens");
    final List<String> rawTokensList = StringUtil.split(rawTokens.trim(), ",");
    for (String rawToken : rawTokensList) {
      final List<String> parts = StringUtil.split(rawToken.trim(), ":");
      final Iterator<String> it = parts.iterator();
      if (!it.hasNext()) {
        throw new RuntimeException("Bad token specification"); //todo
      }
      final String name = it.next().trim();
      if (!it.hasNext()) {
        throw new RuntimeException("Bad token specification"); //todo
      }
      final String token = it.next().trim();
      if (!it.hasNext()) {
        throw new RuntimeException("Bad token specification"); //todo
      }
      final String agentPoolId = it.next().trim();

      final TokenData data = new TokenData(name, token, agentPoolId);
      result.add(data);
    }
    return result;
  }


  public class TokenData {
    private final String myName;
    private final String myToken;
    private final String myAgentPoolId;

    public TokenData(String name, String token, String agentPoolId) {
      myName = name;
      myToken = token;
      myAgentPoolId = agentPoolId;
    }

    public String getName() {
      return myName;
    }

    public String getToken() {
      return myToken;
    }

    public String getAgentPoolId() {
      return myAgentPoolId;
    }
  }
}

/*
 * Seldon -- open source prediction engine
 * =======================================
 *
 * Copyright 2011-2015 Seldon Technologies Ltd and Rummble Ltd (http://www.seldon.io/)
 *
 * ********************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ********************************************************************************************
 */

package io.seldon.clustering.recommender.jdo;

import io.seldon.cc.UserClusterManager;
import io.seldon.clustering.recommender.ClusterCountStore;
import io.seldon.clustering.recommender.ClusterFromReferrerPeer;
import io.seldon.clustering.recommender.CountRecommender;
import io.seldon.clustering.recommender.UserClusterStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JdoCountRecommenderUtils {
	
	@Autowired
	AsyncClusterCountFactory asyncClusterCountFactory;
	
	@Autowired
	ClusterFromReferrerPeer clusterFromReferrerPeer;
	
	public JdoCountRecommenderUtils() {
	}

	public CountRecommender getCountRecommender(String client)
	{
		// Get cluster counter
		ClusterCountStore counter = new JdoClusterCountStore(client,asyncClusterCountFactory); // Database backed count store

		// get user clusters
		UserClusterStore userClusters = null;
		userClusters = UserClusterManager.get().getStore(client); // Hack until we always use this class
		if (userClusters == null)
		{
			JdoMemoryUserClusterFactory memUserFac = JdoMemoryUserClusterFactory.get();
			if (memUserFac != null)
				userClusters = memUserFac.get(client);
			if (userClusters == null) 
				userClusters = new JdoUserClusterStore(client);
		}
		
		return new CountRecommender(client,userClusters,counter,clusterFromReferrerPeer.get(client));
	}

	
}

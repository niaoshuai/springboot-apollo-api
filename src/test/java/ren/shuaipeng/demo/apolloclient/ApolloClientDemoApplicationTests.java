package ren.shuaipeng.demo.apolloclient;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenAppDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *  参考文档
 *
 *  https://www.apolloconfig.com/#/zh/usage/apollo-open-api-platform
 *  https://www.jianshu.com/p/6e243bfa9ed2
 */
@SpringBootTest
class ApolloClientDemoApplicationTests {

	final String portalUrl = ""; // portal url
	final String token = ""; // 申请的token
	final ApolloOpenApiClient client = ApolloOpenApiClient.newBuilder()
			.withPortalUrl(portalUrl)
			.withToken(token)
			.build();


	String appId = "appId";
	String namespace = "application";
	String apolloUser = "apollo";
	String dataTypeYml = "yml";
	String dataContentYml =
			"spring: "+
					"  dataSource:"+
					"  	url: jdbc://123";

	String dataContent = "123";

	@Test
	void getAllApp() {
		List<OpenAppDTO> allApps = client.getAllApps();
		assertTrue(allApps.size() > 0);
	}

	/**
	 * get config data
	 */
	@Test
	void getNamespace(){
		OpenNamespaceDTO dev = client.getNamespace(appId, "DEV", ConfigConsts.CLUSTER_NAME_DEFAULT, namespace);
		System.out.println("dev = " + dev);
	}

	/**
	 * 新增或者更新配置
	 */
	@Test
	void saveOrConfig() throws IOException {
		String propertiesData = "#===================================中间件配置=============================================\n" +
				"\n" +
				"#Redis-Test\n" +
				"cache.type = redis\n" +
				"cache.redis.host = 127.0.0.1\n" ;

		Properties properties = new Properties();
		properties.load(new StringReader(propertiesData));

		// 创建并优化配置
		properties.forEach((key, value) -> {
			OpenItemDTO openItemDTO = new OpenItemDTO();
			openItemDTO.setKey(key.toString());
			openItemDTO.setValue(value.toString());
			openItemDTO.setDataChangeCreatedBy(apolloUser);
			client.createOrUpdateItem(appId,"DEV", ConfigConsts.CLUSTER_NAME_DEFAULT,namespace,openItemDTO);
		});

	}

	@Test
	public void releaseConfig() {
		NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
		namespaceReleaseDTO.setReleasedBy("apollo");
		namespaceReleaseDTO.setReleaseTitle("RELEASE_ITERATION_NO_"+ UUID.randomUUID());
		client.publishNamespace(appId,"DEV",ConfigConsts.CLUSTER_NAME_DEFAULT,namespace, namespaceReleaseDTO);
	}
}

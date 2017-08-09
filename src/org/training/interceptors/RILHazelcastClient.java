/**
 *
 */
package org.training.interceptors;

import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.HazelcastClientNotActiveException;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IMap;


/**
 * @author Srinivas.Sunkara
 *
 */
public class RILHazelcastClient
{
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RILHazelcastClient.class);
	private static HazelcastInstance client;

	public static <K, V> V put(final String mapName, final K key, final V value)
	{
		final IMap<K, V> dataMap = getMap(mapName);
		return dataMap.put(key, value);
	}

	public static <K, V> void putAll(final String mapName, final Map<K, V> map)
	{
		final IMap<K, V> dataMap = getMap(mapName);
		dataMap.putAll(map);
	}

	public static <K, V> V get(final String mapName, final K key)
	{
		final IMap<K, V> dataMap = getMap(mapName);
		return dataMap.get(key);
	}

	public static <K, V> Map<K, V> get(final String mapName, final Set<K> keys)
	{
		final IMap<K, V> dataMap = getMap(mapName);
		return dataMap.getAll(keys);
	}

	public static <K, V, M> V remove(final String mapName, final K key)
	{
		final IMap<K, V> dataMap = getMap(mapName);
		return dataMap.remove(key);
	}


	public static <K, V> IMap<K, V> getMap(final String mapName)
	{
		HazelcastInstance client = getClient();
		IMap<K, V> dataMap = null;
		try
		{
			dataMap = client.getMap(mapName);
		}
		catch (final HazelcastInstanceNotActiveException exp)
		{
			LOG.error("Got HazelcastInstanceNotActiveException, creating new client");
			client = getNewClient();
			dataMap = client.getMap(mapName);
		}
		catch (final HazelcastClientNotActiveException exp)
		{
			LOG.error("Got HazelcastClientNotActiveException, creating new client");
			client = getNewClient();
			dataMap = client.getMap(mapName);
		}
		return dataMap;
	}

	public synchronized static HazelcastInstance getClient()
	{
		if (null == client)
		{
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getGroupConfig().setName(Config.getString("hazelcast.group.name", "SIT"))
					.setPassword(Config.getString("hazelcast.group.password", "SIT-pass"));
			String[] addresses = Config.getString("hazelcast.instance.addresses", "127.0.0.1").split(",");
			Iterator<String> stringIterator = Arrays.stream(addresses).iterator();
			while (stringIterator.hasNext())
			{
				clientConfig.getNetworkConfig().addAddress(stringIterator.next());
			}

			client = HazelcastClient.newHazelcastClient();
		}
		return client;
	}

	public synchronized static HazelcastInstance getNewClient()
	{
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getGroupConfig().setName(Config.getString("hazelcast.group.name", "dev"))
				.setPassword(Config.getString("hazelcast.group.password", "dev-pass"));
		String[] addresses = Config.getString("hazelcast.instance.addresses", "127.0.0.1").split(",");
		Iterator<String> stringIterator = Arrays.stream(addresses).iterator();
		while (stringIterator.hasNext())
		{
			clientConfig.getNetworkConfig().addAddress(stringIterator.next());
		}
		client = HazelcastClient.newHazelcastClient(clientConfig);
		return client;
	}

}

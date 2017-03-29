package kingsheep.team.mchete;

import java.util.Comparator;
import java.util.HashMap;

class ValueComparator<K, V extends Comparable<V>> implements Comparator<K>{
	 
	HashMap<K, V> map = new HashMap<K, V>();
 
	public ValueComparator(HashMap<K, V> map){
		this.map.putAll(map);
	}
 
	@Override
	public int compare(K s1, K s2) {
		return -map.get(s2).compareTo(map.get(s1));//descending order	
	}
}
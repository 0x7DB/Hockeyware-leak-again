package org.hockey.hockeyware.loader;

import java.util.Objects;

public class Pair<K, V>
{
    private final K key;
    private final V value;

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Pair< ?, ? > pair = ( Pair< ?, ? > ) o;
        return Objects.equals( getKey(), pair.getKey() ) && Objects.equals( getValue(), pair.getValue() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getKey(), getValue() );
    }

    public Pair( K key, V value )
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }
}

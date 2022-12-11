package org.hockey.hockeyware.loader;

public class License
{
    private final String name;
    private final String accountType;

    License( String name, String accountType )
    {
        if ( instance != null )
            throw new RuntimeException();
        this.name = name;
        this.accountType = accountType;
        instance = this;
    }

    private static License instance;

    public static License getInstance()
    {
        return instance;
    }

    public String getName()
    {
        return name;
    }

    public String getAccountType()
    {
        return accountType;
    }
}

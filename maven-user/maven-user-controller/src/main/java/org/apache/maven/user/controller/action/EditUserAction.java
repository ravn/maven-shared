package org.apache.maven.user.controller.action;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.user.model.PasswordRuleViolationException;
import org.apache.maven.user.model.PasswordRuleViolations;
import org.apache.maven.user.model.User;
import org.apache.maven.user.model.UserGroup;
import org.apache.maven.user.model.UserManager;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * @author Henry Isidro
 * @version $Id$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="editUser"
 *   instantiation-strategy="per-lookup"
 */
public class EditUserAction
    extends PlexusActionSupport
{

    private static final long serialVersionUID = 8143169847676423348L;

    /**
     * @plexus.requirement
     */
    private UserManager userManager;
    
    private int id;

    private User user;

    private UserGroup userGroup;

    private boolean addMode = false;

    private String username;
    
    private String fullName;

    private String password;

    private String confirmPassword;

    private String email;
    
    private boolean locked;

    private List groups;

    private List allGroups;
    
    public String execute()
        throws Exception
    {
        if( !StringUtils.isEmpty( password ) && !password.equals( confirmPassword ) )
        {
            allGroups = userManager.getUserGroups();
            addActionError( "user.password.mismatch.error" );
            return INPUT;
        }
        if ( addMode )
        {
            if( StringUtils.isNotEmpty( username ) && userManager.getUser( username ) != null )
            {
                addActionError( "user.add.duplicate.username.error" );
                return INPUT;
            }
            
            userGroup = userManager.getDefaultUserGroup();

            user = new User();
            user.setUsername( username );
            user.setFullName( fullName );
            user.setPassword( password );
            user.setLocked( locked );
            user.setEmail( email );
            user.addGroup( userGroup );
            try
            {
                userManager.addUser( user );
            }
            catch ( PasswordRuleViolationException e )
            {
                PasswordRuleViolations violationsContainer = e.getViolations();
                if( violationsContainer != null && violationsContainer.hasViolations() )
                {
                    setActionErrors( violationsContainer.getLocalizedViolations() );
                    return INPUT;
                }
            }
        }
        else
        {
            user = userManager.getUser( id );
            user.setFullName( fullName );
            user.setPassword( password );
            user.setEmail( email );
            user.setLocked( locked );
            if( groups != null )
            {
                user.setGroups( groups );
            }
            
            try
            {
                userManager.updateUser( user );
            }
            catch ( PasswordRuleViolationException e )
            {
                PasswordRuleViolations violationsContainer = e.getViolations();
                if( violationsContainer != null && violationsContainer.hasViolations() )
                {
                    allGroups = userManager.getUserGroups();
                    setActionErrors( violationsContainer.getLocalizedViolations() );
                    return INPUT;
                }
            }
        }

        return SUCCESS;
    }

    public String doAdd()
        throws Exception
    {
        addMode = true;
        return INPUT;
    }

    public String doEdit()
        throws Exception
    {
        addMode = false;
        user = userManager.getUser( id );
        username = user.getUsername();
        fullName = user.getFullName();
        email = user.getEmail();
        locked = user.isLocked();
        groups = user.getGroups();
        allGroups = userManager.getUserGroups();

        return INPUT;
    }
    
    public String editMe()
        throws Exception
    {
        addMode = false;
        user = userManager.getMyUser();
        id = user.getAccountId();
        username = user.getUsername();
        fullName = user.getFullName();
        locked = user.isLocked();
        email = user.getEmail();
        groups = user.getGroups();
        allGroups = userManager.getUserGroups();
    
        return INPUT;
    }
    
    public boolean isAddMode()
    {
        return addMode;
    }

    public void setAddMode( boolean addMode )
    {
        this.addMode = addMode;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getConfirmPassword() 
    {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) 
    {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setGroups( List sgroups )
    {
        groups = new ArrayList();
        
        for( int i = 0; i < sgroups.size(); i++)
        {
            UserGroup dgroup = userManager.getUserGroup( Integer.parseInt(sgroups.get(i).toString()) );
            
            groups.add( dgroup );
        }
    }
    
    public List getAllGroups()
    {
        return allGroups;
    }

    public int[] getSelectedGroups()
    {
        int[] selectedGroups = new int[groups.size()];
        
        for( int i = 0; i < groups.size(); i++)
        {
            selectedGroups[i] = ( (UserGroup) groups.get( i ) ).getId();
        }
        
        return selectedGroups;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void setLocked( boolean isLocked )
    {
        this.locked = isLocked;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

}

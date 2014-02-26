/*******************************************************************************
 * Copyright 2014 Cedric Longo.
 *
 * This file is part of Wall-T program.
 *
 * Wall-T is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Wall-T is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Wall-T.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package utils.teamcity.wallt.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 16/02/14
 *
 * @author Cedric Longo
 */
public final class SavedProjectData {

    @SerializedName( "id" )
    private String _id;

    @SerializedName( "name" )
    private String _name;

    @SerializedName( "parentProjectId" )
    private String _parentId;

    @SerializedName( "alias_name" )
    private String _aliasName;

    public SavedProjectData( final String id, final String name, final String parentId, final String aliasName ) {
        _id = id;
        _name = name;
        _parentId = parentId;
        _aliasName = aliasName;
    }

    public String getId( ) {
        return _id;
    }

    public String getName( ) {
        return _name;
    }

    public String getParentId( ) {
        return _parentId;
    }

    public String getAliasName( ) {
        return _aliasName;
    }
}

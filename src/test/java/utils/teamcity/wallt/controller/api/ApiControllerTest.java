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

package utils.teamcity.wallt.controller.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import utils.teamcity.wallt.TestConfigurationModule;
import utils.teamcity.wallt.ThreadingModule;
import utils.teamcity.wallt.WallApplication;
import utils.teamcity.wallt.controller.api.json.*;
import utils.teamcity.wallt.controller.configuration.ConfigurationModule;
import utils.teamcity.wallt.model.build.*;
import utils.teamcity.wallt.model.configuration.Configuration;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static utils.teamcity.wallt.TestModules.modulesWithOverride;

/**
 * Date: 16/03/14
 *
 * @author Cedric Longo
 */
public class ApiControllerTest {

    @Inject
    private IApiController _apiController;

    @Inject
    private IApiRequestController _mockRequestController;

    @Inject
    private Configuration _configuration;

    @Inject
    private EventBus _eventBus;

    private List<Object> _dispatchedObjects = Lists.newArrayList( );

    @Inject
    private IProjectManager _projectManager;

    @Inject
    private IBuildTypeManager _buildTypeManager;

    @Before
    public void setUp( ) throws Exception {
        Guice.createInjector(
                modulesWithOverride(
                        modulesWithOverride(
                                modulesWithOverride( WallApplication.modules( ), ConfigurationModule.class, new TestConfigurationModule( ) ),
                                ApiRequestModule.class, mockModule( ) ),
                        ThreadingModule.class, testThreadingModule( ) ) )
                .injectMembers( this );

        _configuration.setApiVersion( getApiVersion( ) );
        _eventBus.register( this );
    }

    @Subscribe
    public void objectIsDispatched( final Object object ) {
        _dispatchedObjects.add( object );
    }

    private Module testThreadingModule( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( ExecutorService.class ).toInstance( MoreExecutors.sameThreadExecutor( ) );
                bind( ScheduledExecutorService.class ).toInstance( newScheduledThreadPool( Runtime.getRuntime( ).availableProcessors( ) ) );
            }
        };
    }

    private Module mockModule( ) {
        return new AbstractModule( ) {
            @Override
            protected void configure( ) {
                bind( IApiRequestController.class ).toInstance( mock( IApiRequestController.class ) );
            }
        };
    }

    private ApiVersion getApiVersion( ) {
        return ApiVersion.API_8_1;
    }

    @Test
    public void loadProjectList_starts_a_request_to_controller_with_correct_path( ) throws Exception {
        // Setup
        // Exercise
        _apiController.loadProjectList( );
        // Verify
        verify( _mockRequestController ).sendRequest( getApiVersion( ), "projects", ProjectList.class );
    }

    @Test
    public void loadProjectList_callback_registers_project_to_ProjectManager_and_dispatch_it_on_event_bus( ) throws Exception {
        // Setup
        final ProjectList projectList = new ProjectList( );
        projectList.addProject( new Project( "pId1", "pName", "pParentId" ) );
        projectList.addProject( new Project( "pId2", "pName", "pParentId" ) );

        when( _mockRequestController.sendRequest( getApiVersion( ), "projects", ProjectList.class ) )
                .thenReturn( Futures.immediateFuture( projectList ) );
        // Exercise
        final ListenableFuture<Void> ackFuture = _apiController.loadProjectList( );
        // Verify
        assertThat( _projectManager.getProjects( ).size( ), is( 2 ) );
        assertThat( _projectManager.getProjects( ).get( 0 ).getId( ), is( "pId1" ) );
        assertThat( _projectManager.getProjects( ).get( 1 ).getId( ), is( "pId2" ) );
        assertThat( _dispatchedObjects, hasItem( _projectManager ) );
        assertThat( ackFuture.isDone( ), is( true ) );
    }

    @Test
    public void loadProjectList_callback_registers_exception_on_ack_future( ) throws Exception {
        // Setup
        when( _mockRequestController.sendRequest( getApiVersion( ), "projects", ProjectList.class ) )
                .thenReturn( Futures.immediateFailedFuture( new RuntimeException( "Unexpected test exception" ) ) );
        // Exercise
        final ListenableFuture<Void> ackFuture = _apiController.loadProjectList( );
        // Verify
        try {
            ackFuture.get( );
        } catch ( ExecutionException e ) {
            if ( e.getCause( ).getClass( ) == RuntimeException.class && e.getCause( ).getMessage( ).equals( "Unexpected test exception" ) )
                return;
        }
        TestCase.fail( );
    }

    @Test
    public void loadBuildTypeList_starts_a_request_to_controller_with_correct_path( ) throws Exception {
        // Setup
        // Exercise
        _apiController.loadBuildTypeList( );
        // Verify
        verify( _mockRequestController ).sendRequest( getApiVersion( ), "buildTypes", BuildTypeList.class );
    }

    @Test
    public void loadBuildTypeList_callback_registers_project_to_BuildTypeManager_and_dispatch_it_on_event_bus( ) throws Exception {
        // Setup
        final BuildTypeList buildTypelist = new BuildTypeList( );
        buildTypelist.addBuildType( new BuildType( "bt1", "btName", "btProjectName", "btProjectId" ) );
        buildTypelist.addBuildType( new BuildType( "bt2", "btName", "btProjectName", "btProjectId" ) );

        when( _mockRequestController.sendRequest( getApiVersion( ), "buildTypes", BuildTypeList.class ) )
                .thenReturn( Futures.immediateFuture( buildTypelist ) );
        // Exercise
        final ListenableFuture<Void> ackFuture = _apiController.loadBuildTypeList( );
        // Verify
        assertThat( _buildTypeManager.getBuildTypes( ).size( ), is( 2 ) );
        assertThat( _buildTypeManager.getBuildTypes( ).get( 0 ).getId( ), is( "bt1" ) );
        assertThat( _buildTypeManager.getBuildTypes( ).get( 1 ).getId( ), is( "bt2" ) );
        assertThat( _dispatchedObjects, hasItem( _buildTypeManager ) );
        assertThat( ackFuture.isDone( ), is( true ) );
    }


    @Test
    public void loadBuildTypeList_callback_dispatches_parent_project_on_event_bus( ) throws Exception {
        // Setup
        final ProjectData project = new ProjectData( "p1", "pname", Optional.<String>empty( ) );
        final ProjectData project2 = new ProjectData( "p2", "pname", Optional.<String>empty( ) );
        _projectManager.registerProjects( ImmutableList.of( project, project2 ) );

        final BuildTypeList buildTypelist = new BuildTypeList( );
        buildTypelist.addBuildType( new BuildType( "bt1", "btName", "pname", "p2" ) );

        when( _mockRequestController.sendRequest( getApiVersion( ), "buildTypes", BuildTypeList.class ) )
                .thenReturn( Futures.immediateFuture( buildTypelist ) );
        // Exercise
        _apiController.loadBuildTypeList( );
        // Verify
        assertThat( _dispatchedObjects, not( hasItem( project ) ) );
        assertThat( _dispatchedObjects, hasItem( project2 ) );
    }

    @Test
    public void loadBuildTypeList_callback_registers_exception_on_ack_future( ) throws Exception {
        // Setup
        when( _mockRequestController.sendRequest( getApiVersion( ), "buildTypes", BuildTypeList.class ) )
                .thenReturn( Futures.immediateFailedFuture( new RuntimeException( "Unexpected test exception" ) ) );
        // Exercise
        final ListenableFuture<Void> ackFuture = _apiController.loadBuildTypeList( );
        // Verify
        try {
            ackFuture.get( );
        } catch ( ExecutionException e ) {
            if ( e.getCause( ).getClass( ) == RuntimeException.class && e.getCause( ).getMessage( ).equals( "Unexpected test exception" ) )
                return;
        }
        TestCase.fail( );
    }

    @Test
    public void requestQueuedBuilds_starts_a_request_to_controller_with_correct_path( ) throws Exception {
        // Setup
        // Exercise
        _apiController.requestQueuedBuilds( );
        // Verify
        verify( _mockRequestController ).sendRequest( getApiVersion( ), "buildQueue", QueuedBuildList.class );
    }

    @Test
    public void requestQueuedBuilds_callback_reccords_queued_status_and_dispatches_modified_build_type_on_event_bus( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = new BuildTypeData( "bt1", "btName", "pname", "pId" );
        final BuildTypeData bt2 = new BuildTypeData( "bt2", "btName", "pname", "pId" );
        final BuildTypeData bt3 = new BuildTypeData( "bt3", "btName", "pname", "pId" );
        bt3.setQueued( true );
        final BuildTypeData bt4 = new BuildTypeData( "bt4", "btName", "pname", "pId" );
        bt4.setQueued( true );
        _buildTypeManager.registerBuildTypes( ImmutableList.of( bt1, bt2, bt3, bt4 ) );

        _buildTypeManager.activateMonitoring( bt1 );
        _buildTypeManager.activateMonitoring( bt2 );
        _buildTypeManager.activateMonitoring( bt3 );
        _buildTypeManager.activateMonitoring( bt4 );

        final QueuedBuildList queuedBuildList = new QueuedBuildList( );
        queuedBuildList.addBuildType( new QueueBuild( "bt2" ) );
        queuedBuildList.addBuildType( new QueueBuild( "bt3" ) );
        when( _mockRequestController.sendRequest( getApiVersion( ), "buildQueue", QueuedBuildList.class ) )
                .thenReturn( Futures.immediateFuture( queuedBuildList ) );
        // Exercise
        _apiController.requestQueuedBuilds( );
        // Verify
        assertThat( bt1.isQueued( ), is( false ) );
        assertThat( bt2.isQueued( ), is( true ) );
        assertThat( bt3.isQueued( ), is( true ) );
        assertThat( bt4.isQueued( ), is( false ) );
        assertThat( _dispatchedObjects, not( hasItem( bt1 ) ) );
        assertThat( _dispatchedObjects, hasItem( bt2 ) );
        assertThat( _dispatchedObjects, not( hasItem( bt3 ) ) );
        assertThat( _dispatchedObjects, hasItem( bt4 ) );
    }

    @Test
    public void requestQueuedBuilds_callback_registers_exception_on_ack_future( ) throws Exception {
        // Setup
        when( _mockRequestController.sendRequest( getApiVersion( ), "buildQueue", QueuedBuildList.class ) )
                .thenReturn( Futures.immediateFailedFuture( new RuntimeException( "Unexpected test exception" ) ) );
        // Exercise
        final ListenableFuture<Void> ackFuture = _apiController.requestQueuedBuilds( );
        // Verify
        try {
            ackFuture.get( );
        } catch ( ExecutionException e ) {
            if ( e.getCause( ).getClass( ) == RuntimeException.class && e.getCause( ).getMessage( ).equals( "Unexpected test exception" ) )
                return;
        }
        TestCase.fail( );
    }

    @Test
    public void requestLastBuildStatus_starts_a_request_to_controller_with_correct_path( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = new BuildTypeData( "bt1", "btName", "pname", "pId" );
        // Exercise
        _apiController.requestLastBuildStatus( bt1 );
        // Verify
        verify( _mockRequestController ).sendRequest( getApiVersion( ), "builds/?locator=buildType:bt1,running:any,count:" + ApiController.MAX_BUILDS_TO_CONSIDER, BuildList.class );
    }

    @Test
    public void requestLastBuildStatus_callback_registers_exception_on_ack_future( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = new BuildTypeData( "bt1", "btName", "pname", "pId" );

        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/?locator=buildType:bt1,running:any,count:" + ApiController.MAX_BUILDS_TO_CONSIDER, BuildList.class ) )
                .thenReturn( Futures.immediateFailedFuture( new RuntimeException( "Unexpected test exception" ) ) );
        // Exercise
        final ListenableFuture<Void> ackFuture = _apiController.requestLastBuildStatus( bt1 );
        // Verify
        try {
            ackFuture.get( );
        } catch ( ExecutionException e ) {
            if ( e.getCause( ).getClass( ) == RuntimeException.class && e.getCause( ).getMessage( ).equals( "Unexpected test exception" ) )
                return;
        }
        TestCase.fail( );
    }

    @Test
    public void requestLastBuildStatus_callback_starts_requests_to_retreive_last_build_status( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = new BuildTypeData( "bt1", "btName", "pname", "pId" );
        _buildTypeManager.registerBuildTypes( ImmutableList.of( bt1 ) );
        _buildTypeManager.activateMonitoring( bt1 );

        final BuildList buildList = new BuildList( );
        final Build b12246 = new Build( 12246, new BuildType( "bt1", "btname", "btprojectName", "btProjectid" ), BuildStatus.FAILURE, BuildState.finished, false );
        final Build b12247 = new Build( 12247, new BuildType( "bt1", "btname", "btprojectName", "btProjectid" ), BuildStatus.FAILURE, BuildState.finished, false );
        final Build b12248 = new Build( 12248, new BuildType( "bt1", "btname", "btprojectName", "btProjectid" ), BuildStatus.FAILURE, BuildState.finished, false );
        final Build b12249 = new Build( 12249, new BuildType( "bt1", "btname", "btprojectName", "btProjectid" ), BuildStatus.FAILURE, BuildState.finished, false );
        buildList.addBuild( b12249 );
        buildList.addBuild( b12248 );
        buildList.addBuild( b12247 );
        buildList.addBuild( b12246 );

        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/?locator=buildType:bt1,running:any,count:" + ApiController.MAX_BUILDS_TO_CONSIDER, BuildList.class ) )
                .thenReturn( Futures.immediateFuture( buildList ) );

        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/id:12246", Build.class ) ).thenReturn( Futures.immediateFuture( b12246 ) );
        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/id:12247", Build.class ) ).thenReturn( Futures.immediateFuture( b12247 ) );
        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/id:12248", Build.class ) ).thenReturn( Futures.immediateFuture( b12248 ) );
        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/id:12249", Build.class ) ).thenReturn( Futures.immediateFuture( b12249 ) );
        // Exercise
        _apiController.requestLastBuildStatus( bt1 );
        // Verify
        verify( _mockRequestController ).sendRequest( getApiVersion( ), "builds/?locator=buildType:bt1,running:any,count:" + ApiController.MAX_BUILDS_TO_CONSIDER, BuildList.class );
        verify( _mockRequestController, atLeastOnce( ) ).sendRequest( getApiVersion( ), "builds/id:12249", Build.class );
        verify( _mockRequestController, atLeastOnce( ) ).sendRequest( getApiVersion( ), "builds/id:12248", Build.class );
        verify( _mockRequestController, atLeastOnce( ) ).sendRequest( getApiVersion( ), "builds/id:12247", Build.class );
        verify( _mockRequestController, never( ) ).sendRequest( getApiVersion( ), "builds/id:12246", Build.class );
    }

    @Test
    public void requestLastBuildStatus_callback_records_build_on_build_type( ) throws Exception {
        // Setup
        final BuildTypeData bt1 = new BuildTypeData( "bt1", "btName", "pname", "pId" );
        _buildTypeManager.registerBuildTypes( ImmutableList.of( bt1 ) );
        _buildTypeManager.activateMonitoring( bt1 );

        final BuildList buildList = new BuildList( );
        final Build b12246 = new Build( 12246, new BuildType( "bt1", "btname", "btprojectName", "btProjectid" ), BuildStatus.FAILURE, BuildState.finished, false );
        buildList.addBuild( b12246 );

        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/?locator=buildType:bt1,running:any,count:" + ApiController.MAX_BUILDS_TO_CONSIDER, BuildList.class ) )
                .thenReturn( Futures.immediateFuture( buildList ) );
        when( _mockRequestController.sendRequest( getApiVersion( ), "builds/id:12246", Build.class ) ).thenReturn( Futures.immediateFuture( b12246 ) );
        // Exercise
        _apiController.requestLastBuildStatus( bt1 );
        // Verify
        assertThat( bt1.getBuildById( 12246 ), is( notNullValue( ) ) );
    }

}

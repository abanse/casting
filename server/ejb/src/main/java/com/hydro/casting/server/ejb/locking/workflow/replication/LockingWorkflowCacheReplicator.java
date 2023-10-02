package com.hydro.casting.server.ejb.locking.workflow.replication;

import com.hydro.casting.server.contract.locking.workflow.LockingWorkflowView;
import org.hibernate.Session;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
public class LockingWorkflowCacheReplicator
{
    @EJB
    private LockingWorkflowView lockingWorkflowView;

    @PersistenceContext
    private EntityManager entityManager;

    @Lock( LockType.READ )
    public void replicate() throws InterruptedException
    {
        Session session = entityManager.unwrap( Session.class );
        session.setDefaultReadOnly( true );

        lockingWorkflowView.refreshCache( null );
    }
}
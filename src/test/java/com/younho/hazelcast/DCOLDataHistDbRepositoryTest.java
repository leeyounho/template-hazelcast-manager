package com.younho.hazelcast;

class DCOLDataHistDbRepositoryTest extends AbstractDCOLDataHistRepositoryTest {
    // TODO oracle DB에서 하던가, H2 database에서 동작하도록 수정 필요
    private DCOLDataHistDbRepository dbRepository;

    @Override
    protected DCOLDataHistRepository createRepository() {
        // TODO
        return dbRepository;
    }

    @Override
    protected void cleanup() {
        // TODO
    }
}
Ext.define('erp.store.sys.JobStore', {
	extend: 'Ext.data.Store',
	storeId : 'jobStore',
    fields: ['jo_name'],
    proxy: {
        type: 'ajax',
        async: false,
        url : basePath + 'hr/employee/getJobs.action',
        reader: {
             type: 'json',
             root: 'jobs'
        }
     },
    autoLoad:false
});
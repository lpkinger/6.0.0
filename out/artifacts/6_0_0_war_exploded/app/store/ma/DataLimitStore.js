Ext.define('erp.store.ma.DataLimitStore', {
	extend: 'Ext.data.Store',
	storeId : 'limitStore',
    fields:[{
    	name:'id_'
    },{
    	name:'table_'
    },{
    	name:'desc_'
    }],
    autoLoad:true,
	proxy: {
		type: 'ajax',
		url: basePath+'ma/datalimit/getDataLimits.action',
		method:'get',
		extraParams:{
			all_:1
		},
		reader: {
          type: 'json',
          root: 'employees'
		}
	}
});
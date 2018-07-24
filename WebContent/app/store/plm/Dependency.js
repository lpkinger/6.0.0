Ext.define("erp.store.plm.Dependency", {
	model:'erp.model.plm.Dependency',
	extend : 'Gnt.data.DependencyStore',
	storeId:'gantt.dependency',
	autoLoad : true,
	proxy: {
		type : 'ajax',
		extraParams :{
			condition:'prjplanid='+prjplanid,
		},
		url:basePath+ 'plm/gantt/getdependency.action',
		method: 'GET',
		reader: {
			type : 'json',
			root:'dependency'
		},
		writer: {
            nameProperty: 'mapping'
        }
	}
});
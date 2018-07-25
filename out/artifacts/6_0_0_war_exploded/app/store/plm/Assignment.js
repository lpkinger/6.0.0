Ext.define('erp.store.plm.Assignment', {
	extend : 'Gnt.data.AssignmentStore',
	proxy : {
		method: 'GET',
		type : 'ajax',
		url: basePath+'plm/resourceassignment.action',
		extraParams :{
			condition:'prjplanid='+prjplanid,
		},
		reader : {
			type : 'json',
			root : 'assignments'
		}
	},
	listeners : {
		load : function() {
			this.resourceStore.loadData(this.proxy.reader.jsonData.resources);
		}
	}
});
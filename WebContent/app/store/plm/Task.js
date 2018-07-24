Ext.define("erp.store.plm.Task", {
	model: 'erp.model.plm.Task',
	extend : 'Gnt.data.TaskStore',
	//sorters : ['StartDate','Id'],
	//autoSync:true,
	autoLoad:true,
	cascadeChanges:true,
	proxy : {
		type : 'ajax',
		headers : { "Content-Type" : 'application/json' },
		extraParams :{
			condition:'prjplanid='+prjplanid,
		},
		api: {
			read:    basePath+'plm/gantt.action?Live=' + (typeof(Live) == 'undefined' ? '' : Live),
			create:  basePath+'plm/ganttcreate.action'
		},
		writer : {
			type : 'json',
			encode : true,
			nameProperty:'data',
			allowSingle : false
		},
		reader : {
			type : 'json'
		}
	},
	listeners:{
		'datachanged':function (store){
			
		}

	}
});
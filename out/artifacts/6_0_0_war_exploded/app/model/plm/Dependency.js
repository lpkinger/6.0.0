Ext.define('erp.model.plm.Dependency', {
	extend : 'Gnt.model.Dependency',
	alias: 'widget.GanttDependencyModel',
	fields: [{ name: 'Id', mapping:"DE_ID", type: 'int' },
	         { name: 'From', mapping: 'DE_FROM', type: 'int' },
	         { name: 'To', mapping: 'DE_TO', type: 'int' },
	         { name: "Type", mapping: 'DE_TYPE', type:'int' } ]
});
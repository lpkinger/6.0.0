Ext.define('erp.model.mobile.TreeNode', {
    extend: 'Ext.data.Model',
    config: {fields: [{name:'id',type:'int'}, {name:'text',type:'string'},{name:'parentId',type:'int'},{name:'url',type:'string'}, {name:'qtitle',type:'string'},{name:'leaf',type:'boolean'},{name:'allowDrag',type:'boolean'}]}
});

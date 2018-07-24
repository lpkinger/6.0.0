/*
model层
treeModel
*/
Ext.define('erp.model.TreeModel', {
    extend: 'Ext.data.Model',
    alias: 'widget.erpTreeModel',
    fields: [{name:'id',type:'int'}, 
             {name:'text',type:'string'},
             {name:'parentId',type:'int'},
             {name:'url',type:'string'}, 
             {name:'qtitle',type:'string'},
             {name:'leaf',type:'boolean'},
             {name:'allowDrag',type:'boolean'},
             'children']
});

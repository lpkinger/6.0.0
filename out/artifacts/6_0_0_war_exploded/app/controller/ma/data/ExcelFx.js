Ext.QuickTips.init();
Ext.define('erp.controller.ma.data.ExcelFx', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'ma.data.ExcelFx','core.form.Panel','core.form.ArgsField','core.form.ArgTypeField',
   		'core.button.Add','core.button.Save','core.button.Close',
   		'core.button.Update','core.button.Submit','core.button.Scan','core.toolbar.Toolbar',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.button.DeleteDetail'
   	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'combo[name=ef_argnum]': {
    			change:function(field,newValue,oldValue,eOpts){
    	        	var fieldSet=Ext.getCmp('ef_args');
    	        		fieldSet.removeAll();    	        		
    	        	for(var i=1; i<=newValue; i++){
    	        		fieldSet.add({
    						xtype: 'argtypefield',
    						name: '参数' + i,
    						fieldLabel:'参数'+i,
    						fieldStyle:"background:#fffac0;color:#515151;",
    						columnWidth: 0.33,
    						labelWidth: 50,
    						readOnly: true,    					  					
    						listeners:{
    							
    						}
    					});
    				}
    	        	fieldSet.add({
						xtype: 'hidden',
						name: 'ef_args',
						id:'args',
						fieldStyle:"background:#fffac0;color:#515151;",
						labelWidth: 50,
						readOnly: true,    					  					
					});
    	        }
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.save(btn);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('DataStore', '添加数据集', 'jsps/ma/data/dataStore.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		//get args 赋值
		var items=Ext.getCmp('ef_args').items.items;
		var arr=new Array();
		var argnames=new Array();
		for(var i=0;i<items.length-1;i++){
			arr.push(items[i].value);
			argnames.push(items[i].value.split(";")[0]);
		}
		Ext.getCmp('args').setValue(arr.toString());
		if(Ext.getCmp('ef_code').value == null || Ext.getCmp('ef_code').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		//函数的完整名称赋
		Ext.getCmp('ef_fullname').setValue(Ext.getCmp('ef_name').getValue()+"("+argnames.toString()+")");
		me.FormUtil.beforeSave(me);
	}
});
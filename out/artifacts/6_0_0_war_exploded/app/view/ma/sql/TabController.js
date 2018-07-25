Ext.define('erp.view.ma.sql.TabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.tab-view',
    onExecute: function() {
		var editor=Ext.getCmp('sql_edit').editor,value=editor.getValue(),viewport=Ext.getCmp('Viewport');
		if(!value){
			alert('未输入有效的执行语句！');
			return false;
		}
		viewport.setLoading(true);
		var me=this;
		Ext.Ajax.request({
			url: basePath+'ma/dev/exec.action',
			params:{
				statement:value
			},
			method:'post',
			callback:function(o,success,response){
				viewport.setLoading(false);
				var json= Ext.decode(response.responseText);
				var grid=Ext.getCmp('result-grid');
				if(json.success){
					if(json.list &&  json.list.length>0){
						var o=json.list[0],keys=Object.keys(o),cols=new Array();
						Ext.Array.each(keys,function(k){
							cols.push({
								dataIndex:k,
								text:k
							});
						});						
						grid.setConfig({
							columns:cols,
							store:Ext.create('Ext.data.Store', {										
								fields:keys,								
								pageSize:20,
								autoLoad: true,
								data:json,
								proxy: {
							        type: 'memory',
							        enablePaging:true,
							        reader: {
							            type: 'json',
							            rootProperty: 'list'
							        }
							    }
							})
						});								
					}else  me.truncateTable(grid);							
				}else  me.truncateTable(grid);
				var detail=Ext.getCmp('detail');
				detail.tpl.overwrite(detail.body,json);
			}
		});
	
    },
    truncateTable:function(grid){
    	grid.setConfig({
			columns:[],
			store:Ext.create('Ext.data.Store', {										
				fields:[],								
				pageSize:20,
				autoLoad: true,
				data:[]
			})
		});	
    },
    onExport:function(){
    	Ext.getCmp('result-grid').saveDocumentAs({
    		type:'xlsx',
    		fileName:'导出.xlsx'
    	}).then(null,Ext.emptyFn);
    }
    
});
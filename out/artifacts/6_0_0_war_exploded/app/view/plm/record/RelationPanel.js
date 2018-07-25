Ext.define('erp.view.plm.record.RelationPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.relationPanel',
	layout : 'fit',
	id: 'relation', 
	closeAction:'close',
 	plugins:[  
 	         Ext.create('Ext.grid.plugin.CellEditing',{  
 	         clicksToEdit:1 //设置单击单元格编辑  
 			})  
 	     ], 
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [{
     	header:'单据类型',
    	height:25,
    	flex:0.1,
    	align:'center',
      	dataIndex:'TR_CLASS'
    },{
     	header:'单据描述',
    	height:25,
    	flex:0.3,
    	dataIndex:'TR_REMARK',
		editor : {
			xtype : 'textareatrigger',
			editable:false,
		}
    },{
     	header:'单据状态',
    	height:25,
    	flex:0.07,
    	align:'center',
    	dataIndex:'TR_STATUS'
    },{
    	header:'单据编号',
    	height:25,
    	flex:0.1,
    	align:'center',
    	dataIndex:'TR_KEYCODE'
    },{
    	header:'单据ID',
    	align:'center',
    	height:25,
    	flex:0.07,
    	dataIndex:'TR_KEYID',
    	renderer:function(val,m,record){
    		var url=record.data["TR_RENDER"];
    		if(val){
    			var index = 0, length = url.length, s, e;
    			while(index < length) {
    				if((s = url.indexOf('{', index)) != -1 && (e = url.indexOf('}', s + 1)) != -1) {
    					url = url.substring(0, s) + val + url.substring(e+1);
    					index = e + 1;
    				} else {
    					break;
    				}
    			}
    			return  '<a href="javascript:openUrl(\'' + url + '\');">' + val + '</a>';
    		}else return val;
    	}
    },{
     	header:'单据页面',
    	height:25,
    	flex:0.2,
    	dataIndex:'TR_RENDER',
    	hidden:true,
    }],
    bodyStyle:'background-color:#f1f1f1;',
});
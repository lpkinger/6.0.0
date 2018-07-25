Ext.define('erp.view.oa.myProcess.jprocessMonitoring.HistoryNodeGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpHistoryProcessGridPanel',
	layout : 'auto',
	id: 'historyGrid', 
 	emptyText : '无数据',
 	 title: '历史操作日志',
    columnLines : true,
    autoScroll : true,
    //store: [],
    columns: [],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    nodeId: null,
	initComponent : function(){ 
	    
	    var me = this; 
    	Ext.Ajax.request({
    	    url: basePath + 'common/getProcessInstanceId.action',
    	    params: {
    	    	jp_nodeId : me.nodeId
    	    },
    	    success: function(response){
    	        var text = response.responseText;
    	        var jsonData = Ext.decode(text);
    	        var processInstanceId = jsonData.processInstanceId;
    	        Ext.getCmp("historyGrid").getOwnStore(processInstanceId);
    	       
    	        
    	    }
    	});
		
		this.callParent(arguments); 
	} ,
	getOwnStore: function(processInstanceId){
		
		var me = this;
		
		Ext.Ajax.request({
        	url : basePath + 'common/getAllHistoryNodes.action',
        	params: {
        		
        		processInstanceId:processInstanceId  ,
        		
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		var store = Ext.create('Ext.data.Store', {
        		    storeId: 'gridStore',
        		    fields: [{name: 'jn_id', type: 'string'},
        		             {name: 'jn_name', type: 'string'},
        		             {name: 'jn_dealManId', type: 'string'},
        		            /* {name: 'jn_dealManName', type: 'string'},*/
        		             {name: 'jn_dealManName', type: 'string'},
        		             {name: 'jn_dealTime', type: 'string'},
        		             {name: 'jn_dealResult', type: 'string'},
        		             {name: 'jn_operatedDescription', type: 'string'},
        		             {name: 'jn_nodeDescription', type: 'string'},
        		             {name: 'jn_infoReceiver', type: 'string'},
        		             {name: 'jn_processInstanceId', type: 'string'},
        		    ],
        		    data: res.nodes
        		});
        		
        		var columns = [{header: '节点名称',  dataIndex: 'jn_name'},
        		               {header: '处理人',  dataIndex: 'jn_dealManId'},
        		               /*{header: '处理人姓名', dataIndex: 'jn_dealManName'},*/
        		               {header: '处理日期',  dataIndex: 'jn_dealTime'},
        		               {header: '处理结果',  dataIndex: 'jn_dealResult'},
        		               {header: '操作描述',  dataIndex: 'jn_operatedDescription',flex:1},
        		               {header: '节点描述',  dataIndex: 'jn_nodeDescription'},
        		               {header: '信息接收人',  dataIndex: 'jn_infoReceiver'},
        		               ];
        		Ext.getCmp("historyGrid").reconfigure(store, columns);
        		//Ext.getCmp("pagingtoolbar").bind(store);
        		//Ext.getCmp('pagingtoolbar').updateInfo();
        		//怎么改都不行，只有出此下策，直接改其text
        		//Ext.getCmp('pagingtoolbar').down("#afterTextItem").setText("页,共 "+ Math.ceil(dataCount / pageSize) + " 页");
        		//me.showTbButtons(condition);//toolbar加一些button
        		//拿到datalist对应的单表的关键词
        		//keyField = res.keyField;//form表主键字段
        		//pfField = res.pfField;//grid表主键字段
        		//url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
        	}
        });
	},
	
	
    
});
Ext.define('erp.view.oa.flow.FlowViewPanel', {
	extend: 'Ext.form.Panel', 
	alias : 'widget.FlowViewPanel',
	layout: 'border',
	autoScroll : true,
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	GridUtil:Ext.create('erp.util.GridUtil'),
	initComponent : function() {
		var me = this;
		//读取流程图id
		Ext.Ajax.request({
			url : basePath + 'oa/flow/getFlowChartByCaller.action',
			async: false,
			params:{
				caller : caller
			},
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				me._fcid = rs.data.fcid;
			}
		});
		var items = [{
			tools:[{
				type:'help',
			    tooltip: '信息帮助',
			    handler: function(event, toolEl, panel){
			        Ext.Msg.alert('信息帮助','当前节点不会出现在记录表中，但是派生流程的记录会显示来源处理信息，<br>即处理人是派生该流程的人员信息');
			    }
			}],
			xtype:'grid',
			collapsible:true,
    		id:'view' ,
    		height:160,
    		region:'north',
    		autoScroll:true,
    		title:'流程记录',
    		_noc:1,
    		columns:[{
    			xtype: 'rownumberer',
				text:'序号',
				width:40,
				align :'center'
    		},{
				dataIndex:'FI_NODENAME',
				flex:0.15,
				align:'left',
				style:'text-align:center;',
				text:'节点名称'
			},{
				dataIndex:'FO_NAME',
				flex:0.15,
				align:'left',
				style:'text-align:center;',
				text:'操作',
				renderer: function (value, metaData, record) {
					if(value=='commit') return '提交';
					if(record.get('FO_TYPE')=='Flow') return value+'(派生)';
					if(record.get('FI_NODENAME')=='END') return '自动结束流程';
					else return value;
		        }
			},{
				dataIndex:'FL_NAME',
				flex:0.15,
				align:'left',
				style:'text-align:center;',
				text:'处理人'
			},{
				dataIndex:'FL_CODE',
				flex:0.15,
				align:'left',
				style:'text-align:center;',
				text:'处理人编码'
			},{
				dataIndex:'FL_DEALTIME',
				flex:0.15,
				align:'center',
				text:'处理时间',
				xtype:'datecolumn',
				renderer:function(value, metaData, record){
					if(record.get('FI_NODENAME')=='END') return null;
					else return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			},{
				dataIndex:'FL_STAYTIME',
				flex:0.15,
				align:'right',
				style:'text-align:center;',
				text:'停留时间(分钟)'
			}],
			store:Ext.create('Ext.data.Store',{
			    storeId:'viewstore',
				fields : [ 'FI_NODENAME','FO_NAME','FL_NAME','FL_CODE','FL_DEALTIME','FL_STAYTIME','FO_TYPE'],
			    proxy: {
			        type: 'ajax',
			        url: basePath + '/oa/flow/getHistoryIntance.action',
			        extraParams:{
						caller : caller,
						id : formCondition
			        },
			        reader: {
			            type: 'json',
			            root: 'data'
			        }
			    },
			    autoLoad: false
			}),
			listeners:{
				afterrender:function(grid){
					if(grid.store){
						grid.store.load();
					}
				}
			}
		},{
			tools:[{
				type:'help',
			    tooltip: '操作帮助',
			    handler: function(event, toolEl, panel){
			        Ext.Msg.alert('操作帮助','滚动流程图后，流程记录会自动收缩');
			    }
			}],
			xtype:'panel',
			title:'流程图',
			region:'center',
			tag : 'iframe',
			frame : true,
			border : false,
			html : '<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor2/workfloweditorscan.jsp?fd_fcid='+me._fcid+'&caller='+caller+'&keyvalue='+formCondition+'" height="95%" width="100%" frameborder="0" scrolling="auto"></iframe>'
		}]
		Ext.apply(me, { 
			items:items 
		}); 
		this.callParent(arguments);
	}
});
Ext.define('erp.view.oa.flow.FlowOperationPanel', {
	extend: 'Ext.form.Panel', 
	alias : 'widget.FlowOperationPanel',
	layout: 'anchor',
	autoScroll : true,
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	GridUtil:Ext.create('erp.util.GridUtil'),
	initComponent : function() {
		var me = this;
		var items = [{
			xtype:'grid',
    		id:'operation' ,
    		title:'操作日志',
    		anchor:'100% 100%',
    		autoScroll:true,
    		_noc:1,
    		columns:[{
    			xtype: 'rownumberer',
				text:'序号',
				width:40,
				align :'center'
    		},{
				dataIndex:'FL_NODENAME',
				width:200,
				align:'left',
				style:'text-align:center;',
				text:'节点名称',
				renderer: function (value, metaData, record) {
					if(record.get('FL_TYPE')=='deletefile'){
						return '附件';
					}
					else return value;
		        }
			},{
				dataIndex:'FO_NAME',
				width:150,
				align:'left',
				style:'text-align:center;',
				text:'操作',
				renderer: function (value, metaData, record) {
					if(value=='commit') return '提交';
					if(record.get('FL_TYPE')=='change') return '变更责任人';
					if(record.get('FO_TYPE')=='Flow') return value+'(派生)';
					if(record.get('FL_TYPE')=='deletefile') return '删除附件';
					if(record.get('FL_TYPE')=='ROLLBACK') return '回退节点';
					if(record.get('FL_TYPE')=='moreDuty') return '多责任人审核';
					else return value;
		        }
			},{
				dataIndex:'FL_NAME',
				width:80,
				align:'left',
				style:'text-align:center;',
				text:'处理人'
			},{
				dataIndex:'FL_CODE',
				width:80,
				align:'left',
				style:'text-align:center;',
				text:'处理人编码'
			},{
				dataIndex:'FL_DEALTIME',
				width:180,
				align:'center',
				text:'处理时间',
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			},{
				dataIndex:'FL_REMARK',
				flex:1,
				align:'left',
				style:'text-align:center;',
				text:'备注',
				renderer:function(value, metaData, record){
					if(record.get('FL_TYPE')=='deletefile'){
						backFile = function (params) {
							SaveTwoButton('确定还原该附件？', function(btn){
								if(btn == 'yes'){
									params = params.split(',');
									//更新附件表
									Ext.Ajax.request({
										url : basePath + 'oa/flow/backFile.action',
										params:{
											logid : params[0],
											fileid:params[1],
											id : params[2]
										},
										method : 'POST',
										async: false,
										callback : function(options,success,response){
											var rs = new Ext.decode(response.responseText);
											if(rs.exceptionInfo){
												showError(rs.exceptionInfo);return;
											}
											if(rs.success){
												Ext.getCmp('operation').store.load();
								    			Ext.MessageBox.alert("消息","附件还原成功");
											}
										}
									});
								}else{
									return;
								}
							});
		                }            
			            return value + "<input type='button' value='还原' class='x-btn-gray' style='margin-bottom:3px;cursor:pointer;height:22px;width:60px;margin-left: 5px;' onClick='backFile(\""+record.get('FL_ID')+","+record.get('FL_URL')+","+record.get('FL_KEYVALUE')+"\")') />";
					}
					else if(record.get('FO_TYPE')=='Flow'){
						return Ext.String.format('<a href="javascript:openUrl2(\'{0}\',\'{1}\');" target="_blank">{2}</a>',
									record.get('FL_URL'),record.get('FO_NAME'),'派生来源单据'
							);
					} else return value
				}
			}],
			store:Ext.create('Ext.data.Store',{
			    storeId:'viewstore',
				fields : [ 'FL_ID','FL_FOID','FL_CODE','FL_NAME','FL_DEALTIME','FL_FDSHORTNAME','FL_CODEVALUE',
						   'FL_KEYVALUE','FL_NODENAME','FL_STAYTIME','FL_NODEID','FL_TYPE','FL_URL','FL_REMARK','FO_NAME','FO_TYPE'],
			    proxy: {
			        type: 'ajax',
			        url: basePath + '/oa/flow/getLog.action',
			        extraParams:{
						id : me._id
			        },
			        reader: {
			            type: 'json',
			            root: 'data'
			        }
			    },
			    autoLoad: true
			})
		}]
		Ext.apply(me, { 
			items:items 
		}); 
		this.callParent(arguments);
	}
});
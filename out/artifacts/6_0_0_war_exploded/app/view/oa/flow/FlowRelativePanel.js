Ext.define('erp.view.oa.flow.FlowRelativePanel',{ 
	extend: 'Ext.form.Panel',  
	alias: 'widget.FlowRelativePanel',
	layout: 'fit',
	padding:'10 10 0 10',
	style:'background:#f2f2f2',
	autoScroll : true,
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	GridUtil:Ext.create('erp.util.GridUtil'),
	initComponent : function() {
		var bases=this.getBases(this);
		this.callParent(arguments);
		if(bases) this.add(bases);
	},
	getBases:function(form){
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var id = formCondition.split('=')[1];
		return {
			id:'relation',
			layout: 'vbox',
			xtype:'panel',
			bodyStyle:'border:none;background:#f2f2f2;',
			items:[{
				width:'100%',
				xtype: 'fieldset',
				collapsible: true,
				title:'<img src="' + basePath + 'jsps/common/jprocessDeal/images/normal.png" width=20 style="vertical-align:middle;margin:-4px 0 0 0;">&nbsp;<span style="margin-left:4px;font-weight:bold;font-size:13px;">派生任务</span></img>',
				items:[{
					xtype:'grid',
		    		id:'taskgrid' ,
		    		height : 150,
		    		autoScroll:true,
		    		_noc:1,
					columns:[{
						text:'ID',
						dataIndex:'id',
						width:0
					},{
						align:'left',
						style:'text-align:center;',
						cls : "x-grid-header",
						text: '任务描述',
						dataIndex: 'description',
						flex:1,
						renderer:function(val,meta,record){
							var url='jsps/plm/record/billrecord.jsp?whoami=ResourceAssignment!Bill&formCondition=ra_taskid='+record.get('id');
							return Ext.String.format('<a href="javascript:openUrl2(\'{0}\',\'{1}\');" target="_blank">{2}</a>',
									url,record.get('name'),record.get('description')
							);
						}
					},{
						align:'center',
						text:'截止时间',
						dataIndex:'enddate',
						xtype:'datecolumn'
					},{
						align:'left',
						style:'text-align:center;',
						text:'执行人',
						dataIndex: 'resourcename',
						width:100,
						readOnly:true
					},{
						align:'left',
						style:'text-align:center;',
						text:'当前状态',
						logic:'ignore',
						width:100,
						dataIndex:'handstatus',
						readOnly:true,
						renderer:function(val,mata,record){
							if(record.get('statuscode')=='AUDITED' && val=='已完成'){
								return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" >' + 
								'<span style="color:green;padding-left:2px;">' + val + '</span>';
							}else if(record.get('statuscode')=='AUDITED' && val=='已启动'){
								return '<img src="' + basePath + 'resource/images/renderer/doing.png" >' + 
								'<span style="color:blue;padding-left:2px;">' + val + '</span>';
							}else {
								return '<img src="' + basePath + 'resource/images/renderer/key1.png">'+'<span style="color:#8B8B83;padding-left:2px ">' + val + '<a/></span>';
							}
						}
					}],
					store:Ext.create('Ext.data.Store',{
						fields:['name','id','description','enddate','resourcename','handstatus','handstatuscode','statuscode','type','recorder','recorderid'],
						proxy: {
							type: 'ajax',
							url : basePath+'plm/task/getFormTasks.action',
							extraParams:{
								caller:caller,
								codevalue:form._codevalue
							},
							reader: {
								type: 'json',
								root: 'tasks'
							}
						},
						autoLoad:true,
						sorters:[{property : 'id',
		                          direction: 'ASC'}]
					}),
					listeners:{
						afterrender:function(grid){
							/*if(grid.store.data.items.length==0){
								grid.ownerCt.collapse();
							}*/
						}
					}
				}]
			},{
				width:'100%',
				xtype: 'fieldset',
				collapsible: true,
				margin:'10 0 0 0',
				title:'<img src="' + basePath + 'jsps/common/jprocessDeal/images/normal.png" width=20 style="vertical-align:middle;margin:-4px 0 0 0;">&nbsp;<span style="margin-left:4px;font-weight:bold;font-size:13px;">派生流程</span></img>',
				items:[{
					xtype:'grid',
		    		id:'flowgrid' ,
		    		height : 150,
		    		autoScroll:true,
		    		_noc:1,
		    		columns:[{
		    			xtype: 'rownumberer',
						text:'序号',
						width:40,
						align :'center'
		    		},{
						dataIndex:'FR_FDSHORTNAME',
						flex:0.25,
						align:'left',
						style:'text-align:center;',
						text:'派生流程名称',
						renderer: function (value, metaData, record) {
							if(value&&value!=null){
								var end = value.indexOf('-V');
								if(end>0){
									value = value.substring(0,end);
								}
							}
							return value;
						}
					},{
						dataIndex:'FR_RELATIONCODE',
						flex:0.25,
						align:'left',
						style:'text-align:center;',
						text:'派生单据编号',
						renderer:function(val,meta,record){
							var url='jsps/oa/flow/Flow.jsp?whoami='+record.get('FR_CALLER')+'&formCondition='+record.get('FI_KEYFIELD')+'='+record.get('FR_RELATIONID');
							return Ext.String.format('<a href="javascript:openUrl2(\'{0}\',\'{1}\');" target="_blank">{2}</a>',
									url,record.get('FI_TITLE'),record.get('FR_RELATIONCODE')
							);
						}
					},{
						dataIndex:'FR_RELATIONID',
						width:0,
						align:'center',
						text:'派生单据ID'
					},{
						dataIndex:'FI_TITLE',
						flex:0.25,
						align:'left',
						style:'text-align:center;',
						text:'派生单据标题'
					},{
						dataIndex:'FR_NODENAME',
						flex:0.25,
						align:'left',
						style:'text-align:center;',
						text:'派生节点名称'
					}],
					store:Ext.create('Ext.data.Store',{
						fields :  [ 'FI_KEYFIELD','FR_FDSHORTNAME','FR_NAME','FR_RELATIONCODE','FR_RELATIONID','FR_CALLER','FR_NODENAME','FI_TITLE'],
					    proxy: {
					        type: 'ajax',
					        url: basePath + '/oa/flow/getRelation.action',
					        extraParams:{
								caller : caller,
								id : id,
								nodeId:nodeId
					        },
					        reader: {
					            type: 'json',
					            root: 'flow'
					        }
					    },
					    autoLoad: true
					}),
					listeners:{
						afterrender:function(grid){
							/*if(grid.store.data.items.length==0){
								grid.ownerCt.collapse();
							}*/
						}
					}
				}]
			}]
		}
	}
});
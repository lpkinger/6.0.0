Ext.define('erp.view.oa.task.TaskPanel',{ 
	extend: 'Ext.container.Container', 
	alias: 'widget.TaskPanel', 
	emptyText : '无数据',
	layout:'border',
	columnLines : true,
	autoScroll : true,
	requires:['erp.view.core.trigger.DbfindTrigger','erp.view.core.form.FileField'],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	listeners:{
		itemclick:function(selModel,record){
			if(record.data.handstatus!='已完成')  Ext.getCmp('attachfile').setDisabled(false);
			selModel.ownerCt.GridUtil.onGridItemClick(selModel, record);
		}
	},
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [me._gConfig(me.sourceForm),me._fConfig(me.sourceForm)] 
		}); 
		me.callParent(arguments); 

	},
	_gConfig:function(form){
		var me=this,c =form.down('#' + form.codeField);
		return {
			xtype:'gridpanel',
			title:'任务明细',
			region:'center',
			minHeight:100,
			columnLines:true,
			frame:false,
			columns:[{
				text:'ID',
				dataIndex:'id',
				width:0,
			},{
				cls : "x-grid-header",
				text: '任务描述',
				dataIndex: 'description',
				flex:1
			},{
				text:'截止时间',
				dataIndex:'enddate',
				xtype:'datecolumn'
			},{
				text:'执行人',
				dataIndex: 'resourcename',
				width:100,
				readOnly:true
			},{
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
			},{
				dataIndex:'attachs',
				text:'附件',
				width:0
			}],
			store:Ext.create('Ext.data.Store',{
				fields:['id','name','description','enddate','resourcename','resourcecode','resourceemid','handstatus','handstatuscode','statuscode','type','recorder','recorderid'],
				proxy: {
					type: 'ajax',
					url : basePath+'plm/task/getFormTasks.action',
					extraParams:{
						caller:caller,
						codevalue:c.getValue()
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
				itemclick:me.loadFormRecord
			}

		};
	},
	_fConfig:function(form){
		var me=this,title=form.title,codeValue,url,keyValue;
		if (form.codeField) {
			var c = form.down('#' + form.codeField);
			if (c) {
				codeValue=c.getValue();
			}
			var u = new String(window.location.href);
			u = u.substr(u.indexOf('jsps'));
			url=u;
		}
		if(form.keyField){
			var c=form.down('#'+form.keyField);
			if(c) keyValue=c.getValue();
		}
		return {
			title:'添加任务',
			xtype:'form',
			style:'padding-top:20px',
			region:'south',
			layout:'column',
			defaults:{
				labelAlign:'right',
				columnWidth:0.5,
				margin : '2 2 2 2'
			},
			items: [{
				fieldLabel: '任务名称',
				name: 'name',
				allowBlank: false,
				value:title,
				xtype:'textfield',
			},{
				fieldLabel: '开始时间',
				xtype:'datefield',
				name: 'startdate',
				value:new Date(),
				allowBlank: false
			},{
				fieldLabel:'结束时间',
				xtype:'datefield',
				name:'enddate',
				allowBlank:false
			},{
				xtype:'hidden',
				name:'duration'
			},{
				fieldLabel:'执行人',
				xtype:'dbfindtrigger',
				name:'resourcename',
				id:'resourcename'
			},{
				xtype:'hidden',
				name:'resourcecode',
				id:'resourcecode',
				columnWidth:0
			},{
				fieldLabel:'需要确认',
				xtype:'checkbox',
				name:'type',
				inputValue:1,
				listeners:{
					'change':function(field,newvalue,oldvalue){
						var _f=field.ownerCt;
						if(newvalue){
							_f.down('dbfindtrigger[name=recorder]').show();
						}else _f.down('dbfindtrigger[name=recorder]').hide();
					}
				}
			},{
				fieldLabel:'确认人',
				xtype:'dbfindtrigger',
				hidden:true,
				name:'recorder',
				id:'recorder'
			},{
				fieldLabel:'确认人ID',
				xtype:'hidden',
				name:'recorderid'
			},{
				xtype:'textareafield',
				fieldLabel:'任务描述',
				name:'description',
				columnWidth:1
			},{
				xtype : 'hidden',
				name : 'sourcelink',
				value:url
			},{
				xtype : 'hidden',
				name : 'sourcecode',
				value:codeValue
			},{
				xtype : 'hidden',
				name : 'sourcecaller',
			    value:caller||form.caller
			},{
				xtype:'hidden',
				name:'sourceid',
				value:keyValue
			},{
			   xtype:'hidden',
			   name:'statuscode'
			},{
				xtype:'hidden',
				name:'id'
			}],
			buttons: [ {
				text: '保存',
				itemId:'savetask',
				formBind: true,
				disabled: true,
				handler: function(btn) {
					me.onTaskAdd(btn.ownerCt.ownerCt);
				}
			},{
				text: '重置',
				handler: function() {
					this.up('form').getForm().reset();
				}
			},{
				text:'关闭',
				handler:function(btn){
					btn.ownerCt.ownerCt.ownerCt.ownerCt.close();
				}
			}]

		}
	},
	onTaskAdd : function(form) {
		var me = this,status=form.down('field[name=statuscode]'),url='plm/task/saveFormTask.action';
		if(status!=null && status.getValue()=='AUDITED') {
			alert('任务已启动无法修改!');
			return ;
		}
		var start = form.down('field[name=startdate]'),
		end = form.down('field[name=enddate]'),
		dur = form.down('field[name=duration]'),
		name = form.down('field[name=resourcename]'),taskId=form.down('field[name=id]').getValue();
		dur.setValue(Ext.Number.toFixed((end.getValue().getTime() - start.getValue().getTime())/(1000*60*60), 2));
		var v = form.getValues();
		Ext.each(Ext.Object.getKeys(v), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete v[k];
			}
		});
	    if(taskId!=null && taskId!='' && taskId!=0) url="plm/task/updateFormTask.action";
		Ext.Ajax.request({
			url : basePath + url,
			params : {
				formStore : unescape(Ext.encode(v).replace(/\\/g,"%"))
			},
			callback : function(opt, s, res) {
				form.setLoading(false);				
				if (res.responseText=='success') {
					alert(taskId!=null?'添加成功!':'修改成功!');
					form.getForm().reset();
					form.ownerCt.down('gridpanel').getStore().load();
				} else {
					var r = Ext.decode(res.responseText);
					showError(r.exceptionInfo);
				}
			}
		});
	},
	loadFormRecord:function(view,record){
		this.ownerCt.down('form').getForm().loadRecord(record);
	}
});
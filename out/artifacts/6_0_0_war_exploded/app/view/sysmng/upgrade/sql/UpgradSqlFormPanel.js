

Ext.define('erp.view.sysmng.upgrade.sql.UpgradSqlFormPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.upgradSqlForm',
	id:'upgradsqlform',
	border: false, 
	autoScroll:true,
	layout:'column',
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle :'background:#F2F2F2;',
	fieldDefaults : {
		labelAlign : "left",
		labelWidth:60,
		columnWidth:0.4,
		margin:'20 10 5 50',
		msgTarget: 'side',
		trackResetOnLoad : true,
		blankText : $I18N.common.form.blankText
	},
	initComponent : function(){
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		var items  = [{
				xtype:'textfield',
				allowBlank:true,
				dataIndex:'NUM_',
				fieldLabel:'ID',																			
				id:'NUM_',
				name:'NUM_',
				value:'',
				trackResetOnLoad : true,
				hidden:true
			},{
				xtype:'textfield',
				allowBlank:false,
				labelStyle:'color:#FF0000',
				fieldLabel:'创建人',
				id:'MAN_',
				name:'MAN_',
				value:em_name
			},{
				xtype:'datetimefield',
				allowBlank:true,
				fieldLabel:'创建时间',	
				id:'DATE_',
				name:'DATE_',
				format:'Y-m-d H:i:s',
				value:Ext.Date.format(new Date(),"Y-m-d H:i:s")
			},{
				xtype:'numberfield',
				labelStyle:'color:#FF0000',
				allowBlank:false,
				fieldLabel:'版本号',
				id:'VERSION_',
				name:'VERSION_',
				minValue: 0,
				msgTarget :'只能输入大于0的数字',
				value:''
			},{
				xtype:'combo',
				allowBlank:true,
				dataIndex:'STATUS_',
				fieldLabel:'状态',	
				id:'STATUS_',
				name:'STATUS_',
				store: {
					    fields: ['display','value'],
					    data : [					    	
					        {display:'审核通过', value:1},
					        {display:'未审核通过',value:0}
						]
				},
			    queryMode: 'local', 
			    displayField: 'display',
			    valueField: 'value',
				readOnly:true,
				value:0
			},{
				xtype:'textarea',
				allowBlank:true,
				autoScroll:true,
				dataIndex:'DESC_',
				fieldLabel:'说明',
				height:window.innerHeight*0.15,
				columnWidth:0.8,
				value:'',
				id:'DESC_',
				name:'DESC_'
			},{
				xtype:'textarea',
				autoScroll:true,
				allowBlank:true,
				dataIndex:'SQL_',
				fieldLabel:'升级语句',
				value:'',
				id:'SQL_',
				name:'SQL_',
				height:window.innerHeight*0.4,
				columnWidth:0.8
			},{
				xtype:'panel',
				layout:'hbox',
				margin:'40',
				border: false, 
				autoShow: true,
				autoHeight:true,
				columnWidth:0.9,
				buttonAlign : 'center',
				buttons: [{
		            xtype:'erpAddButton',
		            height:26,
		            hidden:true
	        	},{
		            xtype:'erpSaveButton',
		            height:26
	        	},{
		            xtype:'erpUpdateButton',
		            height:26,
		            hidden:true
	        	},{
		            xtype:'erpDeleteButton',
		            height:26,
		            hidden:true
	        	},{
		            xtype:'button',
		            text:'测试',
		            id:'testbtn',
		            iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	width: 60,
			    	style: {
			    		marginLeft: '10px'
			        },
		            height:26
	    		},{
		            xtype:'erpCloseButton',
		            height:26
	        	}]
			}];

		Ext.apply(this,{
			items:items
		});
		
		if(formCondition)
			this.getData(formCondition);
			
		this.callParent(arguments); 
		},
	getData:function(con){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({//拿到form的data
			url : basePath + 'upgrade/getUpgradeSql.action',
			params: {
				condition: con
			},
			method : 'post',
			callback : function(options, success, response){
				me.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);
					return;
				}
				if(res){
					me.getForm().setValues(res);
					me.fireEvent('aftersetvalue', me);
					Ext.getCmp("addbtn").fireEvent('aftersetvalue', Ext.getCmp("addbtn"));
					Ext.getCmp("save").fireEvent('aftersetvalue', Ext.getCmp("save"));
					Ext.getCmp("updatebutton").fireEvent('aftersetvalue', Ext.getCmp("updatebutton"));
					Ext.getCmp("deletebutton").fireEvent('aftersetvalue', Ext.getCmp("deletebutton"));
				}
			}
		})
	}
	
});
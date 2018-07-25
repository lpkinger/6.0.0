Ext.define('erp.view.ma.datalimit.LimitForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.limitform',
	hideBorders: true, 
	id:'limitform',
	frame:true,
	autoScroll:true,
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		ui: 'footer',
		items: [{
			text: '保存权限',
			itemId:'save',
			id:'save',
			iconCls:'x-button-icon-save',
			cls: 'x-btn-gray',
			formBind: true
		},'-',{
			text:'复制权限',
			itemId:'copy',
			cls: 'x-btn-gray',
			iconCls:'x-button-icon-copy'
		},'-',{
			text:'删除权限',
			itemId:'delete',
			cls:'x-btn-gray',
			iconCls:'x-button-icon-deletedetail'
		},'-',{
			text:'同步权限',
			itemId:'datasync',
			cls: 'x-btn-gray',
			iconCls:'x-button-icon-paste'
		}]
	}],
	items:[{
		xtype: 'container',
		layout: 'hbox',
		margin:'0 0 10',
		items: [{
			xtype: 'fieldset',
			flex: 1,
			layout: 'anchor',
			defaults: {
				anchor: '100%',
				hideEmptyLabel: false
			},
			items: [{
				xtype: 'radiogroup',
				fieldLabel:'用户类型',
				defaults: {
					name: 'type_',
					margins: '0 15 0 0'
				},
				items: [{
					inputValue: 'employee',
					boxLabel: '人员',
					id:'empradio',
					checked: true,
					listeners:{
					'afterrender':function(){
					if(em_position==null){
					this.setValue(true);
					}else{
					this.setValue(false);
					}
					}
					}					
				}, {
					inputValue: 'job',
					boxLabel: '岗位',	
					id:'jobradio',
					checked: false,
					listeners:{
					'afterrender':function(){
					if(em_position==null){
					this.setValue(false);
					}else{
					this.setValue(true);
					}
					}
					}
				}]
			},{
				xtype:'multifield',
				fieldLabel:'当前用户',
				allowBlank:false,
				name:'emcode_',
				id:'emcode_',
				secondname:'emname_',
				listeners:{
					'afterrender':function(){
						if(emcode_!=null){
								this.setValue(emcode_);
						}
						if(emname_!=null){
							Ext.getCmp('emname_').setValue(emname_);
						}						
					}
					
				}
			},{
				xtype:'hidden',
				name:'empid_',
				id:'empid_',
				listeners:{
				'afterrender':function(){
				if(empid_!=null){
							Ext.getCmp('empid_').setValue(empid_);
						}
				}
				}
			},{
				xtype:'multifield',
				fieldLabel:'当前岗位',
				allowBlank:true,
				disabled:true,
				name:'jocode_',
				id:'jocode_',
				secondname:'joname_',
				listeners:{
					'afterrender':function(){
						if(jo_code!=null){
							this.setValue(jo_code);
						}
						if(jo_code!=null){
							Ext.getCmp('joname_').setValue(em_position);
						}					
					}
					
					
				}
			},{
				xtype:'hidden',
				name:'jobid_',
				id:'jobid_',
				listeners:{
				'afterrender':function(){
				if(empid_!=null){
							Ext.getCmp('jobid_').setValue(empid_);
						}
				}
				}
			},{
				xtype:'hidden',
				name:'instanceid_',
				id:'instanceid_',
				value:0
			}]
		}, {
			xtype: 'component',
			width: 10
		},{
			xtype:'checkbox',
			flex:1,
			name:'nolimit_',
			id:'nolimit_',
			inputValue:1,
			boxLabel:'拥有当前数据类型的全部数据权限',
			checked:true,
			disabled:true
		}]
	},{
		xtype: 'container',
		layout: 'column',
		margin:'0 0 10',
		items:[{
			xtype: 'fieldset',
			flex: 1,
			columnWidth:0.5,
			layout: 'anchor',
			defaults: {
				anchor: '100%',
				hideEmptyLabel: false
			},
			items:[{
				xtype:'fieldcontainer',
				layout:'hbox',				
				hideLabel:true,
				items:[{
					xtype:'combo',
					flex:1,
					margin: '5 0 0 0',
					fieldLabel:'数据类型',
					allowBlank:false,
					name:'limit_id_',
					id:'limit_id_',
					listConfig:{
						maxHeight:180
					},
					store: {
						fields: ['desc_', 'id_','table_'],
						//data :[]
							autoLoad : true,
									ASYNC:false,
									proxy : {
										type : 'ajax',
										url : basePath
												+ 'ma/datalimit/getDataLimits.action',
										method : 'get',
										extraParams : {
											all_ : 1
										},
										reader : {
											type : 'json',
											root : 'employees'
										}
									}
					},
					displayField: 'desc_',
					valueField: 'id_',
					queryMode: 'local',
					editable:false,
					onTriggerClick:function(trigger){
						var me=this,store=this.getStore();
						if (!me.readOnly && !me.disabled) {
							if (me.isExpanded) {
								me.collapse();
							} else {
								me.expand();
							}
							me.inputEl.focus();
						}    
					},
					listeners:{
					'afterrender':function(){
						if(limitid_!=null){
							this.setValue(limitid_);
						}		
						if(jobid_!=null){
							Ext.getCmp('jobid_').setValue(jobid_);
						}
						if(instanceid_!=null){
							Ext.getCmp('instanceid_').setValue(instanceid_);
						}
					}
					}
				},{
					xtype:'button',
					iconCls: 'x-button-icon-data',
					margin: '5 0 0 5',
					padding:'0 0 2 0',
					width:22,
					tooltip:'选择数据',
					cls: 'x-btn-gray',
					disabled:true,
					itemId:'select'
				}]

			},{
				xtype:'radiogroup',
				fieldLabel:'授权类型',
				id:'limittype_',
				defaults: {
					name: 'limittype_',
					margins: '0 15 0 0'
				},
				items:[{
					inputValue: 'detail',
					boxLabel: '按明细数据授权',
					id:'detradio',
					checked: true
				},{
					inputValue: 'condition',
					id:'condradio',
					boxLabel:'按条件语句授权'
				}/*{
					inputValue: 'parent',
					boxLabel:'按上级组授权',
					disabled:true
				}*/]
			},{
				xtype:'textareafield',
				fieldLabel:'条件语句',
				name:'condition_',
				id:'condition_',
				hidden:true,
				readOnly:true
			}]
		},{
			flex:1,
			columnWidth:0.5,
			margin: '0 0 0 10',
			xtype:'checkbox',
			name:'noaddlimit_',
			id:'noaddlimit_',
			inputValue:1,
			boxLabel:'自动具有新增加数据的全部权限',
			disabled:true
		},{
			xtype:'checkbox',
			flex:1,
			margin: '0 0 0 10',
			columnWidth:0.5,
			name:'usereport_',
			id:'usereport_',
			inputValue:1,
			boxLabel:'报表查询进行数据检查',
			disabled:true
		}]
	}],
	initComponent : function(){
		var me=this;
		me.callParent(arguments);
		emname_ = getUrlParam('emname_');
		emcode_ =getUrlParam('emcode_');
		desc_1 =getUrlParam('desc_1');
		em_position=getUrlParam('em_position');
		jo_code=getUrlParam('jo_code');
		limitid_=getUrlParam('limitid_');
		empid_=getUrlParam('empid_');
		jobid_=getUrlParam('jobid_');
		instanceid_=getUrlParam('instanceid_');
	}
});
Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.Workovertime', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.attendance.Workovertime','core.form.Panel','core.form.FileField','core.form.MultiField','core.grid.Panel2',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','core.button.TurnMake', 'core.button.End','core.button.ResEnd',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','core.form.DateHourMinuteField',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.button.Confirm',
  			'core.button.InsertEmployee','hr.attendance.EmpTree2','core.form.DateHourMinuteField','core.button.ModifyDetail','erp.view.core.button.Modify','core.form.HrOrgSelectField'
  	],
	init:function(){
		var me = this;
		this.control({
    		'erpGridPanel2': { 
    			itemclick:function(selModel, record){
    				me.onGridItemClick(selModel, record);
    			}
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpInsertEmployeeButton' : {
				click: function(btn){
					var filter = me.openInsertWin(btn);
					filter.show();
				}
			},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('wo_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('wo_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wo_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addWorkovertime', '新增加班申请', 'jsps/hr/attendance/workovertime.jsp?whoami=Workovertime');
				}
			},
			'erpEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE' && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onEnd(crid);
				}
			},
			'erpResEndButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResEnd(crid);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wo_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('wo_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wo_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('wo_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wo_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAuditWithManAndTime(Ext.getCmp('wo_id').value,'wo_auditer','wo_auditdate');
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('wo_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAuditWithManAndTime(Ext.getCmp('wo_id').value,'wo_auditer','wo_auditdate');
				}
			},
			'erpConfirmButton': {afterrender: function(btn){
				var statu = Ext.getCmp('wo_statuscode');
				if(statu && statu.value != 'AUDITED'){
					btn.hide();
				}
			},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('wo_id').value);
    				
    			}
    		},   		
    		'dbfindtrigger[name=wo_cop]':{
    			afterrender: function(t){
    			t.autoDbfind=false;
    			}
    		},
    		'dbfindtrigger[name=wod_empcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				if(Ext.getCmp('wo_cop')){
    					var code = Ext.getCmp('wo_cop').value;
    					if(code != null && code != ''){
    						var obj = me.getCodeCondition();
        					if(obj && obj.field){
        						t.dbBaseCondition = obj.field + "='" + code + "'";
        					}
    					}
    				}
    			},
    			aftertrigger: function(t){
    				if(Ext.getCmp('wo_cop')){
    					var obj = me.getCodeCondition();
    					if(obj && obj.fields){
    						me.FormUtil.getFieldsValue(obj.tablename, obj.fields, obj.myfield + "='" + t.value + "'", obj.tFields);
    					}
    				}
    			}
    		}
    		



    		
    		
    		
    	});
	},
	getCodeCondition: function(){
		var field = "em_cop";
		var tFields = 'wo_cop';
		var fields = 'em_cop';
		var tablename = 'Employee';
		var myfield = 'em_code';
		var obj = new Object();
		obj.field = field;
		obj.fields = fields;
		obj.tFields = tFields;
		obj.tablename = tablename;
		obj.myfield = myfield;
		return obj;
	},
	openInsertWin: function(btn){
    	var me = this;
    	var filter = null;
    	if(caller == 'Workovertime'){
    		//加班
    		filter = Ext.create('Ext.Window', {
        		id: btn.getId() + '-filter',
        		style: 'background:#f1f1f1',
        		title: '插入员工',
        		width: 800,
        		modal:true,
        		height: 500,
        	    layout: 'column',
        	    defaults: {
        	    	margin: '5 5 5 5'
        	    },
        	    items: [
        	            {
        	    	
        	            	xtype:'form',
        	            	columnWidth: 1,
        	            	id:'win-form',
        	            	layout: 'column',
        	        	    defaults: {
        	        	    	margin: '5 5 5 5'
        	        	    },
        	            	items:[
        	       	            { xtype:'datefield', name:'startdate', id:'startdate', columnWidth: .33, fieldLabel: '起始时间', labelWidth: 70  }, 
        	    	            { xtype:'datefield', name:'enddate', id:'enddate', columnWidth: .33, fieldLabel: '结束时间', labelWidth: 70  },		//起始 结束时间
        	    	            { xtype:'combo', name:'type', id:'type', columnWidth: .33, fieldLabel: '请假类型', labelWidth: 70, displayField:'dftext',valueField:'vftext',queryMode:'local',store:{fields:['dftext','vftext'],data:[{'dftext':'普通加班','vftext':'普通加班'},{'dftext':'双休日加班','vftext':'双休日加班'},{'dftext':'节假日加班','vftext':'节假日加班'}]} },
        	    	            { xtype : 'timefield', labelWidth: 70, name : 'jias1', id : 'jias1', fieldLabel : '加上1', columnWidth: .5, format: 'H:i', increment: 30 },
        	    	            { xtype : 'timefield', name : 'jiax1', labelWidth: 70, id : 'jiax1', fieldLabel : '加下1', columnWidth: .5, format: 'H:i', increment: 30 },		//加上1，加下1
        	    	            { xtype : 'timefield', name : 'jias2', labelWidth: 70, id : 'jias2', fieldLabel : '加上2', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax2', labelWidth: 70, id : 'jiax2', fieldLabel : '加下2', columnWidth: .5, format: 'H:i', increment: 30 },		//加上2，加下2
        	    	            { xtype : 'timefield', name : 'jias3', labelWidth: 70, id : 'jias3', fieldLabel : '加上3', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax3', labelWidth: 70, id : 'jiax3', fieldLabel : '加下3', columnWidth: .5, format: 'H:i', increment: 30 },		//加上3，加下3
        	    	            { xtype : 'timefield', name : 'jias4', labelWidth: 70, id : 'jias4', fieldLabel : '加上4', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax4', labelWidth: 70, id : 'jiax4', fieldLabel : '加下4', columnWidth: .5, format: 'H:i', increment: 30 }		//加上4，加下4
        	            	       ]
        	    		},

    					{ 
        	            	xtype: 'EmpTree2', 
        	            	columnWidth: .5, 
        	            	margin: '5 5 5 5', 
        	            	height:250,
        					listeners:{
        						checkchange: function(node,checked){
        							if(node.data.leaf){
        								var dialog = this.ownerCt;
        								var grid = dialog.items.items[2];
        								if(checked){
        									me.loadNodeToGrid(node,grid);
        								}else{
        									me.removeNodeFromGrid(node,grid);
        								}
        							}
        						}
        					}
    					},{
        						columnWidth: .5,
        						xtype:'grid',
        						id:'select_grid',
        						margin: '5 5 5 5',
        						height:250,
        						columns:[
        						         {header:'员工编号',dataIndex:'em_code'},
        						         {header:'员工姓名',dataIndex:'em_name'},
        						         {header:'员工id',dataIndex:'em_id'}
        						         ]
        					
        					} /* */ //组织树， 选中列表
        	          ],
    			buttonAlign: 'center',
        	    buttons: [{
    	    		text: '确定',
    	    		width: 60,
    	    		cls: 'x-btn-blue',
    	    		handler: function(btn) {
    	    			
    	    			var fl = btn.ownerCt.ownerCt;
    	    			var form = fl.items.items[0];
    	    			var grid = fl.items.items[2];
    	    			
    	    			var formValues = form.getValues();
    	    			var gridValues = grid.getStore().getNewRecords();
    	    			me.createBaseGridData(formValues,gridValues);
    	    			fl.items.items[2].store.removeAll();
    	    			fl.destroy();
    	    		}
    	    	},{
    	    		text: '关闭',
    	    		width: 60,
    	    		cls: 'x-btn-blue',
    	    		handler: function(btn) {
    	    			
    	    			var fl = btn.ownerCt.ownerCt;
    	    			fl.items.items[2].store.removeAll();
    	    			fl.destroy();
    	    		
    	    		}
    	    	}]
        	});
    	}else if (caller == 'Workovertime!Straight'){
    		//直落
    		filter = Ext.create('Ext.Window', {
        		id: btn.getId() + '-filter',
        		style: 'background:#f1f1f1',
        		title: '插入员工',
        		width: 800,
        		modal:true,
        		height: 500,
        	    layout: 'column',
        	    defaults: {
        	    	margin: '5 5 5 5'
        	    },
        	    items: [
        	            {
        	    	
        	            	xtype:'form',
        	            	columnWidth: 1,
        	            	id:'win-form',
        	            	layout: 'column',
        	        	    defaults: {
        	        	    	margin: '5 5 5 5'
        	        	    },
        	            	items:[
        	       	            { xtype:'datefield', name:'startdate', id:'startdate', columnWidth: .5, fieldLabel: '起始时间', labelWidth: 70  }, 
        	    	            { xtype:'datefield', name:'enddate', id:'enddate', columnWidth: .5, fieldLabel: '结束时间', labelWidth: 70  },		//起始 结束时间
//        	    	            { xtype:'combo', name:'type', id:'type', columnWidth: .33, fieldLabel: '请假类型', labelWidth: 70, displayField:'dftext',valueField:'vftext',queryMode:'local',store:{fields:['dftext','vftext'],data:[{'dftext':'普通加班','vftext':'普通加班'},{'dftext':'双休日加班','vftext':'双休日加班'},{'dftext':'节假日加班','vftext':'节假日加班'}]} },
        	    	            { xtype : 'timefield', name : 'jias1', labelWidth: 70, id : 'jias1', fieldLabel : '直落开始1', columnWidth: .5, format: 'H:i', increment: 30 },
        	    	            { xtype : 'timefield', name : 'jiax1', labelWidth: 70, id : 'jiax1', fieldLabel : '直落结束1', columnWidth: .5, format: 'H:i', increment: 30 },		//加上1，加下1
        	    	            { xtype : 'timefield', name : 'jias2', labelWidth: 70, id : 'jias2', fieldLabel : '直落开始2', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax2', labelWidth: 70, id : 'jiax2', fieldLabel : '直落结束2', columnWidth: .5, format: 'H:i', increment: 30 },		//加上2，加下2
        	    	            { xtype : 'timefield', name : 'jias3', labelWidth: 70, id : 'jias3', fieldLabel : '直落开始3', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax3', labelWidth: 70, id : 'jiax3', fieldLabel : '加下结束3', columnWidth: .5, format: 'H:i', increment: 30 },		//加上3，加下3
        	    	            { xtype : 'timefield', name : 'jias4', labelWidth: 70, id : 'jias4', fieldLabel : '直落开始4', columnWidth: .5, format: 'H:i', increment: 30 },
        	    	            { xtype : 'timefield', name : 'jiax4', labelWidth: 70, id : 'jiax4', fieldLabel : '加下结束4', columnWidth: .5, format: 'H:i', increment: 30 }		//加上4，加下4
        	            	       ]
        	    		},

    					{ 
        	            	xtype: 'EmpTree2', 
        	            	columnWidth: .5, 
        	            	margin: '5 5 5 5', 
        	            	height:250,
        					listeners:{
        						checkchange: function(node,checked){
        							if(node.data.leaf){
        								var dialog = this.ownerCt;
        								var grid = dialog.items.items[2];
        								if(checked){
        									me.loadNodeToGrid(node,grid);
        								}else{
        									me.removeNodeFromGrid(node,grid);
        								}
        							}
        						}
        					}
    					},{
        						columnWidth: .5,
        						xtype:'grid',
        						id:'select_grid',
        						margin: '5 5 5 5',
        						height:250,
        						columns:[
        						         {header:'员工编号',dataIndex:'em_code'},
        						         {header:'员工姓名',dataIndex:'em_name'},
        						         {header:'员工id',dataIndex:'em_id'}
        						         ]
        					
        					} /* */ //组织树， 选中列表
        	          ],
    			buttonAlign: 'center',
        	    buttons: [{
    	    		text: '确定',
    	    		width: 60,
    	    		cls: 'x-btn-blue',
    	    		handler: function(btn) {
    	    			
    	    			var fl = btn.ownerCt.ownerCt;
    	    			var form = fl.items.items[0];
    	    			var grid = fl.items.items[2];
    	    			
    	    			var formValues = form.getValues();
    	    			var gridValues = grid.getStore().getNewRecords();
    	    			me.createBaseGridData(formValues,gridValues);
    	    			fl.items.items[2].store.removeAll();
    	    			fl.destroy();
    	    		}
    	    	},{
    	    		text: '关闭',
    	    		width: 60,
    	    		cls: 'x-btn-blue',
    	    		handler: function(btn) {
    	    			
    	    			var fl = btn.ownerCt.ownerCt;
    	    			fl.items.items[2].store.removeAll();
    	    			fl.destroy();
    	    		
    	    		}
    	    	}]
        	});
    	}else if (caller == 'Workovertime!WholeNight'){
    		//通宵
    		filter = Ext.create('Ext.Window', {
        		id: btn.getId() + '-filter',
        		style: 'background:#f1f1f1',
        		title: '插入员工',
        		width: 800,
        		modal:true,
        		height: 500,
        	    layout: 'column',
        	    defaults: {
        	    	margin: '5 5 5 5'
        	    },
        	    items: [
        	            {
        	    	
        	            	xtype:'form',
        	            	columnWidth: 1,
        	            	id:'win-form',
        	            	layout: 'column',
        	        	    defaults: {
        	        	    	margin: '5 5 5 5'
        	        	    },
        	            	items:[
        	       	            { xtype:'datefield', name:'startdate', id:'startdate', columnWidth: .33, fieldLabel: '起始时间', labelWidth: 70  }, 
        	    	            { xtype:'datefield', name:'enddate', id:'enddate', columnWidth: .33, fieldLabel: '结束时间', labelWidth: 70  },		//起始 结束时间
//        	    	            { xtype:'combo', name:'type', id:'type', columnWidth: .33, fieldLabel: '请假类型', labelWidth: 70, displayField:'dftext',valueField:'vftext',queryMode:'local',store:{fields:['dftext','vftext'],data:[{'dftext':'普通加班','vftext':'普通加班'},{'dftext':'双休日加班','vftext':'双休日加班'},{'dftext':'节假日加班','vftext':'节假日加班'}]} },
        	    	            { xtype : 'timefield', labelWidth: 70, name : 'jias1', id : 'jias1', fieldLabel : '通宵开始1', columnWidth: .5, format: 'H:i', increment: 30 },
        	    	            { xtype : 'timefield', name : 'jiax1', labelWidth: 70, id : 'jiax1', fieldLabel : '通宵结束1', columnWidth: .5, format: 'H:i', increment: 30 },		//加上1，加下1
        	    	            { xtype : 'timefield', name : 'jias2', labelWidth: 70, id : 'jias2', fieldLabel : '通宵开始2', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax2', labelWidth: 70, id : 'jiax2', fieldLabel : '通宵结束2', columnWidth: .5, format: 'H:i', increment: 30 },		//加上2，加下2
        	    	            { xtype : 'timefield', name : 'jias3', labelWidth: 70, id : 'jias3', fieldLabel : '通宵开始3', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax3', labelWidth: 70, id : 'jiax3', fieldLabel : '通宵结束3', columnWidth: .5, format: 'H:i', increment: 30 },		//加上3，加下3
        	    	            { xtype : 'timefield', name : 'jias4', labelWidth: 70, id : 'jias4', fieldLabel : '通宵开始4', columnWidth: .5, format: 'H:i', increment: 30 },	
        	    	            { xtype : 'timefield', name : 'jiax4', labelWidth: 70, id : 'jiax4', fieldLabel : '通宵结束4', columnWidth: .5, format: 'H:i', increment: 30 }		//加上4，加下4
        	            	       ]
        	    		},

    					{ 
        	            	xtype: 'EmpTree2', 
        	            	columnWidth: .5, 
        	            	margin: '5 5 5 5', 
        	            	height:250,
        					listeners:{
        						checkchange: function(node,checked){
        							if(node.data.leaf){
        								var dialog = this.ownerCt;
        								var grid = dialog.items.items[2];
        								if(checked){
        									me.loadNodeToGrid(node,grid);
        								}else{
        									me.removeNodeFromGrid(node,grid);
        								}
        							}
        						}
        					}
    					},{
        						columnWidth: .5,
        						xtype:'grid',
        						id:'select_grid',
        						margin: '5 5 5 5',
        						height:250,
        						columns:[
        						         {header:'员工编号',dataIndex:'em_code'},
        						         {header:'员工姓名',dataIndex:'em_name'},
        						         {header:'员工id',dataIndex:'em_id'}
        						         ]
        					
        					} /* */ //组织树， 选中列表
        	          ],
    			buttonAlign: 'center',
        	    buttons: [{
    	    		text: '确定',
    	    		width: 60,
    	    		cls: 'x-btn-blue',
    	    		handler: function(btn) {
    	    			
    	    			var fl = btn.ownerCt.ownerCt;
    	    			var form = fl.items.items[0];
    	    			var grid = fl.items.items[2];
    	    			
    	    			var formValues = form.getValues();
    	    			var gridValues = grid.getStore().getNewRecords();
    	    			me.createBaseGridData(formValues,gridValues);
    	    			fl.items.items[2].store.removeAll();
    	    			fl.destroy();
    	    		}
    	    	},{
    	    		text: '关闭',
    	    		width: 60,
    	    		cls: 'x-btn-blue',
    	    		handler: function(btn) {
    	    			
    	    			var fl = btn.ownerCt.ownerCt;
    	    			fl.items.items[2].store.removeAll();
    	    			fl.destroy();
    	    		
    	    		}
    	    	}]
        	});
    	}
    	
		return filter;
    
	},
	
	
	createBaseGridData: function(win_form_values, win_grid_store ){
		var me = this;
//		var array = new Array();
		var base_grid = Ext.getCmp('grid');
		var last_detno = 0;
		var last_index = -1;
		
		var old_store = base_grid.getStore().getNewRecords();
		var old_array = new Array();
		Ext.each(old_store,function(item,index){
			if(item.data[base_grid.necessaryField] &&item.data[base_grid.necessaryField] != null &&Ext.String.trim( item.data[base_grid.necessaryField])!=''){
				if(item.data['wod_detno'] > last_detno ){
					last_detno = item.data['wod_detno'];
				}
				last_index ++;
				old_array.push(item);
			}
		});
		
		console.log(old_array);
		

		
		Ext.each(win_grid_store,function(item,index){
			var record = base_grid.getStore().getAt(last_index+1);
			if(record||record == null ){
				me.GridUtil.add10EmptyItems(base_grid,1);
				record = base_grid.getStore().getAt(last_index+1);
			}
			if(win_form_values.hasOwnProperty('startdate')){record.set('wod_startdate',win_form_values.startdate);}
			if(win_form_values.hasOwnProperty('enddate')){record.set('wod_enddate',win_form_values.enddate);}
			if(win_form_values.hasOwnProperty('type')){record.set('wod_type',win_form_values.type);}
			if(win_form_values.hasOwnProperty('jias1')){record.set('wod_jias1',win_form_values.jias1);}
			if(win_form_values.hasOwnProperty('jiax1')){record.set('wod_jiax1',win_form_values.jiax1);}
			
			if(win_form_values.hasOwnProperty('jias2')){record.set('wod_jias2',win_form_values.jias2);}
			if(win_form_values.hasOwnProperty('jiax2')){record.set('wod_jiax2',win_form_values.jiax2);}
			
			if(win_form_values.hasOwnProperty('jias3')){record.set('wod_jias3',win_form_values.jias3);}
			if(win_form_values.hasOwnProperty('jiax3')){record.set('wod_jiax3',win_form_values.jiax3);}
			
			if(win_form_values.hasOwnProperty('jias4')){record.set('wod_jias4',win_form_values.jias4);}
			if(win_form_values.hasOwnProperty('jiax4')){record.set('wod_jiax4',win_form_values.jiax4);}
			
			record.set('wod_detno',last_detno+1);
			record.set('wod_empcode',item.data.em_code);
			record.set('wod_empname',item.data.em_name);
			last_detno++;
			last_index++;
		});
//		
//		for(var i =0; i<array.length;i++){
//			base_grid.store.removeAt(last_index+1);
//		}
//		base_grid.store.insert(last_index+1,array);
		
	},
	
	/**
	 * 从grid 中删除树节点
	 * @param node
	 * @param grid
	 */
	removeNodeFromGrid : function(node,grid){
		var em_id = node.raw.id;
		Ext.each(grid.store.data.items,function(item,index){
			if(item.data.em_id==em_id){
				grid.store.remove([item]);
			}
		});
	},
	
	/**
	 * 向grid 中插入选中树节点
	 * @param node
	 * @param grid
	 */
	loadNodeToGrid : function(node,grid){
		var o = new Object();
		o['em_id'] = node.raw.id;
		o['em_code'] = node.raw.code;
		o['em_name'] = node.raw.qtip;
		grid.store.loadData([o],true);
		
		
	},
	
	
	onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getGriddata: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			jsonGridData.push(Ext.JSON.encode(data));
		}
		return jsonGridData;
	},
	check:function(){
		if(Ext.getCmp('wo_startdate').value>Ext.getCmp('wo_enddate').value){
			showError('结束时间不能小于开始时间！');return true;
		}
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var flag=false;
		Ext.each(items,function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['ppd_costid']==null||item.data['ppd_costid']==0){
					showError('第'+item.data['ppd_detno']+'行的费用类型没选或无效！');flag=true;
				}
				if(item.data['ppd_standardamount']<item.data['ppd_amount']){
					showError('第'+item.data['ppd_detno']+'行的预算费用超出标准金额！');flag=true;
				}
			}
		});
		return flag;
	},
	turnTask:function(id){
		Ext.Ajax.request({
        	url : basePath + '/crm/marketmgr/turnTask.action',
        	params: {id:id},
        	method : 'post',
        	async:false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		//showMessage("提示", '生成任务成功！');
        		//window.location.reload();
        	}
        });
	},
	onConfirm: function(id){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + form.confirmUrl,
	   		params: {
	   			id: id,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				//audit成功后刷新页面进入可编辑的页面 
    				//auditSuccess(function(){
	   					window.location.reload();
	   				//});    				
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '确认成功');
    	   					//auditSuccess(function(){
    	   						window.location.reload();
    	   					//});
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	}
});

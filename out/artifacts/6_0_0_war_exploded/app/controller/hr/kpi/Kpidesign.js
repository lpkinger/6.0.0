Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.Kpidesign', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.kpi.Kpidesign','core.form.Panel','core.grid.Panel2','hr.kpi.GradeDesignGrid','hr.kpi.KpidesigngradeLevelGrid','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit','core.trigger.MultiDbfindTrigger2',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit','erp.view.core.button.AddDetail','core.form.HrOrgSelectField',
  			'erp.view.core.button.DeleteDetail','erp.view.core.button.Copy','erp.view.core.button.Paste','erp.view.core.button.Up',
      		'erp.view.core.button.Down','erp.view.core.button.UpExcel','core.button.VoCreate','core.button.TurnBorrow',
  			'core.button.TurnCustomer','core.button.Flow','core.button.DownLoad','core.button.Scan','common.datalist.Toolbar','hr.kpi.Kpitoolbar',
  			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
  	],
	init:function(){
		var me = this;
		me.gridLastSelected = null;
		this.control({
			'tabpanel':{
				afterrender:function(){
					 Ext.defer(function(){
						 if(!Ext.getCmp('kd_id').value){
							 Ext.getCmp('kpitab').hide();
						 }
					 },1000);
				},
				tabchange:function(t){
					var tabid=t.getActiveTab().id;
					if(tabid!='grid'){
						if(Ext.getCmp('kd_id').value){
							if(!Ext.getCmp('grid').store.data.items[0].data.ki_id){
									showError("请先添加考核项目");
									t.setActiveTab(0);
								}else{
									var records=Ext.getCmp('grid').store.data.items;
									var score=0;
									 Ext.each(records, function(item){
										 score+=item.data['ki_score_to']-0;
									 });
									if(score!=100){
										showError("考核项目最高分之和应为100分");
										t.setActiveTab(0);
									}
									if(tabid!='Kpidesignpoint_F'){
										if(!Ext.getCmp('Kpidesignpoint_F').store.data.items[0].data.kp_id){
											showError("请先添加评分设计");
											t.setActiveTab(1);
										}else{
											var records=Ext.getCmp('Kpidesignpoint_F').store.data.items;
											var percent=0;
											 Ext.each(records, function(item){
												 percent+=item.data['kp_percent']-0;
											 });
											if(percent!=100){
												showError("评分设计权重之和应为100%");
												t.setActiveTab(1);
											}
										}
									}
								}
						}else{
							showError("请先保存");
							t.setActiveTab(0);
						}
						
					}
				}
			},
			'erpGridPanel2[id=kpi-grid]': { 
				  'afterrender':function(grid){
					 Ext.defer(function(grid){
						 if(Ext.getCmp('kp_id')){
							if(Ext.getCmp('kp_id').value){
								if(Ext.getCmp('kp_kidetno')){
								 var detno=Ext.getCmp('kp_kidetno').value;
								 var arr= new Array();
								 arr=detno.split(',');
								 for (i=0;i<arr.length ;i++ ){ 
									 var records=grid.store.data.items;
									 Ext.each(records, function(item){
										if(item.data['ki_detno']==arr[i]){
											var row=grid.getStore().indexOf(item);
											 grid.getSelectionModel().select(row,true); 
										} 
									 });
								    }
								}
							}else{
								grid.getSelectionModel().selectAll()
							}
						 }
						 },1000,this,[grid]);
				    }
				},
    		'erpGridPanel2[id=grid]': { 
    			itemclick :this.onGridItemClick
    		},
    		'field[name=ki_gradetype]' : {
				afterrender : function(f) {
					Ext.getCmp('ki_sql').hide();
					if (f.getValue() == 'calculate') {
						Ext.getCmp('ki_sql').show();
					}
				},
				change : function(f) {
					if (f.getValue() == 'calculate') {
						Ext.getCmp('ki_sql').show();
					}else{
						Ext.getCmp('ki_sql').hide();
					}
				}
			},
    		'combo[name=kd_starttype]':{
				afterrender:function(c){
					Ext.getCmp('kd_startkind').hide();
					Ext.getCmp('kd_startnum').hide();
					if(c.getValue()=='automatic'){
						Ext.getCmp('kd_startkind').show();
						Ext.getCmp('kd_startnum').show();
					}
				},
				select:function(c) {
					var type=c.getValue();
					if(type!='automatic'){
						Ext.getCmp('kd_startkind').hide();
						Ext.getCmp('kd_startnum').hide();
					}else{
						Ext.getCmp('kd_startkind').setValue('season');
						Ext.getCmp('kd_startkind').show();
						Ext.getCmp('kd_startnum').show();
					}
				}
			},
    		'GradeDesignGrid': {
    			itemclick: this.onGridItemClick2
    		},
    		'KpidesigngradeLevelGrid': { 
    			itemclick: this.onGridItemClick3
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					//保存之前的一些前台的逻辑判定
					if(Ext.getCmp('kd_bemanid').value==''){
						showError("受评人不能为空");
					}else{
						this.FormUtil.beforeSave(this);
					}				
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('kd_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addKpidesign', '新增考核模板 ', 'jsps/hr/kpi/Kpidesign.jsp');
				}
			},
			
			'erpCloseButton': {
				click: function(btn){
					var win=parent.Ext.getCmp('singlewin');	
					if(win)
					win.close();
					else
					me.FormUtil.beforeClose(me);					
					}		
			},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kd_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('kd_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kd_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('kd_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kd_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    				if(status && status.value != 'ENTERING'){
    					Ext.getCmp('Kpitoolbar1-KpiAdd').hide();
    					Ext.getCmp('Kpitoolbar2-KpiAdd').hide();
    					Ext.getCmp('Kpitoolbar3-KpiAdd').hide();
    					Ext.getCmp('Kpitoolbar1-KpiUpdate').hide();
    					Ext.getCmp('Kpitoolbar2-KpiUpdate').hide();
    					Ext.getCmp('Kpitoolbar3-KpiUpdate').hide();
    					Ext.getCmp('Kpitoolbar1-KpiDel').hide();
    					Ext.getCmp('Kpitoolbar2-KpiDel').hide();
    					Ext.getCmp('Kpitoolbar3-KpiDel').hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('kd_id').value, true, this.beforeUpdate, this);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kd_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('kd_id').value);
    			}
    		}
    	});
	},
	onGridItemClick2: function(selModel,record){
    	this.onGridItemClick(selModel,record,'GradeDesignGrid');
    	
    },
    onGridItemClick3: function(selModel,record){
    	this.onGridItemClick(selModel,record,'KpidesigngradeLevelGrid');
    	
    },
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},	
	onGridItemClick: function(selModel, record){//grid行选择
		var grid = selModel.ownerCt;
		var f=grid.down('Kpitoolbar').id;
		if(grid && !grid.readOnly && !grid.NoAdd){
			var btn = grid.down('Kpitoolbar').getComponent(f+'-KpiDel');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('Kpitoolbar').getComponent(f+'-KpiUpdate');
			if(btn)
				btn.setDisabled(false);
		}
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
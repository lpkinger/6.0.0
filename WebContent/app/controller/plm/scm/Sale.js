Ext.QuickTips.init();
Ext.define('erp.controller.plm.scm.Sale', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','plm.scm.Sale','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.button.Consign','core.button.End','core.button.ResEnd','core.button.TurnNotify','core.button.FeatureDefinition','core.button.FeatureView',
      			'core.button.LoadFitting', 'core.button.OutSchedule','core.button.MrpOpen','core.button.MrpClose',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.SpecialContainField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				if(record.data.sd_id != 0 && record.data.sd_id != null && record.data.sd_id != ''){
    					var btn = Ext.getCmp('featuredefinition');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('featureview');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('bomopen');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('outschedule');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('updatepmc');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('splitSaleButton');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('MrpOpen');
						btn && btn.setDisabled(false); 
						btn = Ext.getCmp('MrpClose');
						btn && btn.setDisabled(false); 
						//设置载入配件按钮
					    var status = Ext.getCmp('sa_statuscode');
        				if(status && status.value == 'ENTERING'){      		
						    btn = Ext.getCmp("loadFittingbutton");
						    btn && btn.setDisabled(false); 
						}
					}  
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
    		'field[name=sa_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=sa_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber(caller);
					}
    				this.beforeSaveSale();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('sa_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSale', '试产订单', 'jsps/plm/scm/sale.jsp');
    			}
    		},
    		/**
    		 * BOM多级展开 
    		 */
    		'#bomopen': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    
				      var pr_code=record.data.sd_prodcode;
						var url="jsps/pm/bom/BOMStructQuery.jsp?whoami=BOMStruct!Struct!Query";
						var condition="";
						//母件编号带出展开的料号不对  参照万利达配置
						if(pr_code){
						   condition+="pr_codeIS'"+pr_code+"'";
						}
						me.FormUtil.onAdd('BOMStruct'+ pr_code, 'BOM多级展开', url+"&condition="+condition);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onEnd(Ext.getCmp('sa_id').value);
    			}
    		},
    		'combo[name=sa_source]':{
    			change:function (field){
    				var href=window.location.href;
    				var arrstr=href.split("?");
    				if(formCondition!="" &&formCondition!=null){
    					window.location.href =arrstr[0]+'?whoami=Sale!PLM!'+field.value+ '&formCondition=' + 
       					formCondition + '&gridCondition=' + gridCondition+'&source='+field.value;
    				}else window.location.href =arrstr[0]+'?whoami=Sale!PLM!'+field.value+ '&source='+field.value;
    				
    			}   			
    		},
    		'erpResEndButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'FINISH'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResEnd(Ext.getCmp('sa_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('sa_id').value);
    			}
    		},
    		'field[name=sa_statuscode]': {
    			change: function(f){
    				var grid = Ext.getCmp('grid');
    				if(grid && f.value != 'ENTERING' && f.value != 'COMMITED'){
    					grid.setReadOnly(true);//只有未审核的订单，grid才能编辑
    				}
    			}
    		},
    		'erpConsignButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'dbfindtrigger[name=sd_batchcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var pr = record.data['sd_prodcode'];
    				if(pr == null || pr == ''){
    					showError("请先选择料号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var id = record.data['sd_warehouseid'];
        				if(id == null || id == ''){
        					id = Ext.getCmp('sd_warehouseid');
        					if(id == null || id == '' || id== 0 ){
        						showError("请先选择仓库!");
            					t.setHideTrigger(true);
            					t.setReadOnly(true);
        					}
        				} else {
        					t.dbBaseCondition = "ba_warehouseid='" + id + "' AND ba_prodcode='" + pr + "'";
        				}
    				}
    			}
    		},
    		'erpFeatureDefinitionButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.sd_prodcode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.sd_prodcode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
    									var win = new Ext.window.Window({
    			    						id : 'win',
    			    						title: '生成特征料号',
    			    						height: "90%",
    			    						width: "70%",
    			    						maximizable : true,
    			    						buttonAlign : 'center',
    			    						layout : 'anchor',
    			    						items: [{
    			    							tag : 'iframe',
    			    							frame : true,
    			    							anchor : '100% 100%',
    			    							layout : 'fit',
    			    							html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    			    							"jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show();    									
    								} else {
    									showError('物料特征必须为虚拟特征件');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'erpFeatureViewButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.sd_prodcode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.sd_prodcode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'SPECIFIC'){
    									var win = new Ext.window.Window({
    										id : 'win' + record.data.sd_id,
    										title: '特征查看',
    										height: "90%",
    										width: "70%",
    										maximizable : true,
    										buttonAlign : 'center',
    										layout : 'anchor',
    										items: [{
    											tag : 'iframe',
    											frame : true,
    											anchor : '100% 100%',
    											layout : 'fit',
    											html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    											"jsps/pm/bom/FeatureValueView.jsp?fromwhere=SaleDetail&formid=" + record.data.sd_id + '&pr_code=' + record.data.sd_prodcode +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    										}]
    									});
    									win.show();    									
    								} else {
    									showError('物料特征必须为 虚拟特征件');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		/**
    		 * BOM多级展开 
    		 */
    		'#bomopen': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    
				      var pr_code=record.data.sd_prodcode;
						var url="jsps/pm/bom/BOMStructQuery.jsp?whoami=BOMStruct!Struct!Query";
						var condition="";
						//母件编号带出展开的料号不对  参照万利达配置
						if(pr_code){
						   condition+="pr_codeIS'"+pr_code+"'";
						}
						me.FormUtil.onAdd('BOMStruct'+ pr_code, 'BOM多级展开', url+"&condition="+condition);
    			}
    		},
    		'erpOutScheduleButton': {
    			click: function() {
    				var grid = Ext.getCmp('grid'),record = grid.selModel.lastSelected;
    				if(record) {
    					me.schedule(record);
    				}
    			}
    		},
    		/**
    		 * 订单分拆
    		 */
    		'#splitSaleButton': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.SaleSplit(record);
    			}
    		},
    		/**
    		 * 更改PMC日期
    		 */
    		'#updatepmc': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.UpdatePmc(record);
    			}
    		},
    		/**
    		 * BOM多级展开 
    		 */
    		'#bomopen': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    
				      var pr_code=record.data.sd_prodcode;
						var url="jsps/pm/bom/BOMStructQuery.jsp?whoami=BOMStruct!Struct!Query";
						var condition="";
						//母件编号带出展开的料号不对  参照万利达配置
						if(pr_code){
						   condition+="pr_codeIS'"+pr_code+"'";
						}
						me.FormUtil.onAdd('BOMStruct'+ pr_code, 'BOM多级展开', url+"&condition="+condition);
    			}
    		},
    		/**
    		 * 载入配件
    		 * 
    		 */
    		'#loadFittingbutton': {
    			click: function(btn) {
    				//新产生的配件明细，存下原始订单序号到SD_MAKEID。
    				//载入之前判断此序号是否已经存在sd_makeid相等的明细行，如果有就不让再载入
    				var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    var pr_code = record.data.sd_prodcode;
    			    var detno = record.data.sd_detno;
    			    var sa_id = Ext.getCmp('sa_id').value;   			    
    			    var sd_qty = record.data.sd_qty;
    			    me.loadFitting (pr_code,sd_qty,sa_id,detno);
    			}
    		},
    		'erpMrpOpenButton' : {
    			click: function(btn){
					var grid = Ext.getCmp('grid');
					var record = grid.selModel.lastSelected;
					var id = record.data.sd_id;
					if (id && id>0){
						Ext.Ajax.request({
							url : basePath + "scm/sale/saleMrpOpen.action",
							params: {
								id:id,
								caller:caller
							},
							method : 'post',
							async: false,
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
									return;
								}
								showError("打开Mrp成功！");
							}
						});
					}
					
				}
    		},
    		'erpMrpCloseButton' : {
    			click: function(btn){
					var grid = Ext.getCmp('grid');
					var record = grid.selModel.lastSelected;
					var id = record.data.sd_id;
					if (id && id>0){
						Ext.Ajax.request({
							url : basePath + "scm/sale/saleMrpClose.action",
							params: {
								id:id,
								caller:caller
							},
							method : 'post',
							async: false,
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
									return;
								}
								showError("关闭Mrp成功！");
							}
						});
					}
					
				}
    		}
    	});
    },
    getSetting : function(fn) {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'SalePLMType\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			var t = false;
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success && r.data){
    				t = r.data == 'true';
	   			}
    			fn.call(me, t);
	   		}
		});
	},
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getRecordByPrCode: function(){
    	if(this.gridLastSelected && this.gridLastSelected.findable){
    		var data = Ext.getCmp('grid').store.data.items[this.gridLastSelected.index].data;
    		var code = data.pd_prodcode;
    		if(code != null && code!= ''){//看用户输入了编号没有
            	var str = "sd_prodcode='" + code + "'";
            	this.GridUtil.getRecordByCode({caller: 'Sale', condition: str});	
    		}
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveSale: function(){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items,sacode=Ext.getCmp('sa_code').value;
		var bool = true;
		//数量不能为空或0
		var recorddate = Ext.Date.format(Ext.getCmp('sa_recorddate').value, 'Ymd');
		Ext.each(items, function(item){
			item.set('sd_code', sacode);
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_qty'] == null || item.data['sd_qty'] == '' || item.data['sd_qty'] == '0'
					|| item.data['sd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的数量为空');return;
				}
			}
		});	
		var basedate=null,index=0;
		Ext.each(items, function(item){
			item.set('sd_code',sacode);	  
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_delivery'] == null){
					if(index!=0){
						item.set('sd_delivery',basedate);
					}else {
					bool=false;
					showError('明细行第'+item.data['sd_detno']+'行交货日期为空  不能更新!');
					return ;
					}
					
				} else if(Ext.Date.format(item.data['sd_delivery'], 'Ymd') < recorddate){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的交货日期小于单据录入日期');return;
				}else {
					basedate=item.data['sd_delivery'];
				}
			index++;
			}
		});
		//保存sale
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid');
		var sacode = Ext.getCmp('sa_code').value;
		var items = grid.store.data.items;
		var bool = true;
		var recorddate = Ext.Date.format(Ext.getCmp('sa_recorddate').value, 'Ymd');
		//数量不能为空或0
		Ext.each(items, function(item){
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_qty'] == null || item.data['sd_qty'] == '' || item.data['sd_qty'] == '0'
					|| item.data['sd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的数量为空');return;
				}
			}
		});	
		var recorddate = Ext.Date.format(Ext.getCmp('sa_date').value, 'Ymd');
		var basedate=null,index=0;
		Ext.each(items, function(item){
			item.set('sd_code',sacode);	  
			if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
				if(item.data['sd_delivery'] == null){
					if(index!=0){
						item.set('sd_delivery',basedate);
					}else {
					bool=false;
					showError('明细行第'+item.data['sd_detno']+'行交货日期为空  不能更新!');
					return ;
					}
					
				} else if(Ext.Date.format(item.data['sd_delivery'], 'Ymd') < recorddate){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的交货日期小于单据录入日期');return;
				}else {
					basedate=item.data['sd_delivery'];
				}
			index++;
			}
		});
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	},
	/**
	 * 排程
	 */
	schedule: function(record) {
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
	   		height = Ext.isIE ? screen.height*0.75 : '100%';
		var sd_id = record.get('sd_id');
		Ext.Ajax.request({
			url : basePath + "scm/sale/checkSaleDetailDet.action",
			params: {
				whereString: "sd_id="+sd_id
			},
			method : 'post',
			async: false,
			callback:function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
			}
		});
		Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			layout: 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/sale/saleDetail.jsp?formCondition=sd_id=' 
					+ sd_id + '&gridCondition=sdd_sdid=' + sd_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
	},
	/**
	 *销售订单拆分
	 * */
	SaleSplit:function(record){
		var me=this,originaldetno=Number(record.data.sd_detno);
		var said=record.data.sd_said;
		var sdid=record.data.sd_id;
		var sync = Ext.getCmp('sa_sync');
		if(sync && sync.value == '已同步'){
			showError('订单已抛转不能进行拆分操作!');
			return;
		}
		Ext.create('Ext.window.Window',{
    		width:850,
    		height:'80%',
    		iconCls:'x-grid-icon-partition',
    		title:'<h1>销售订单拆分</h1>',
    		id:'win',
    		items:[{
    			xtype:'form',
    			layout:'column',
    			region:'north',
    			frame:true,
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.5,
    				readOnly:true,
    				fieldStyle:'background:#f0f0f0;border: 0px solid #8B8970;color:blue;'
    			},
    			items:[{
    			 fieldLabel:'销售单号',
    			 value:record.data.sd_code,
    			 id:'sacode'
    			},{
    			 fieldLabel:'产品编号'	,
    			 value:record.data.sd_prodcode
    			},{
    			 fieldLabel:'产品名称',
    			 value:record.data.pr_detail
    			},{
    			  fieldLabel:'公司型号',
    			  value:record.data.sd_companytype
    			},{
    			 fieldLabel:'原序号'	,
    			 value:record.data.sd_detno
    			},{
    		     fieldLabel:'原数量',
    		     value:record.data.sd_qty,
    		     id:'sdqty'
    			}],
    			buttonAlign:'center',
    			buttons:[{
    				xtype:'button',
    				columnWidth:0.12,
    				text:'保存',
    				width:60,
    				iconCls: 'x-button-icon-save',
    				margin:'0 0 0 30',
    				handler:function(btn){
    				   var store=Ext.getCmp('smallgrid').getStore();
    				   var count=0;
    				   var jsonData=new Array();
    				   var dd; 
    				   var remainqty;
    				   Ext.Array.each(store.data.items,function(item){
    					  if(item.data.sd_qty!=0 && item.data.sd_qty>0){
    						  if(item.dirty){
    							  dd=new Object();
    							  //说明是新增批次
    							  if(item.data.sd_delivery!=null && item.data.sd_delivery)
    								  dd['sd_delivery']=Ext.Date.format(item.data.sd_delivery, 'Y-m-d');
    							  if(item.data.sd_pmcdate)
    								  dd['sd_pmcdate']=Ext.Date.format(item.data.sd_pmcdate, 'Y-m-d');
    							  dd['sd_qty']=item.data.sd_qty; 
    							  dd['sd_id']=item.data.sd_id;
    							  dd['sd_detno']=item.data.sd_detno;
    							  jsonData.push(Ext.JSON.encode(dd));
    							  if(item.data.sd_pmcdate){
    								  if(Ext.Date.format(item.data.sd_pmcdate, 'Y-m-d') <Ext.Date.format(new Date(), 'Y-m-d') ){
      		    				   		showError('PMC回复日期必须大于等于系统当前日期!') ;  
      			    					return;
    								  }
    							  } 
    							  if(item.data.sd_id!=0&&item.data.sd_id!=null&&item.data.sd_id>0){
    								  remainqty=item.data.sd_qty; 
  								  }
    						  }
    						  count+=Number(item.data.sd_qty);
    					  } 
    				   });	  
    				   var assqty=Ext.getCmp('sdqty').value;
    				   if(count!=assqty){
	    					showError('分拆数量必须等于原数量!') ;  
	    					return;
    				   }else{
    					   var r=new Object();
        				   r['sd_id']=record.data.sd_id;
        				   r['sd_said']=record.data.sd_said;
        				   r['sd_detno']=record.data.sd_detno;  
        				   if(record.data.sd_pmcdate)
        					   r['sd_pmcdate']=Ext.Date.format(record.data.sd_pmcdate,'Y-m-d');
        				   if(record.data.sd_delivery)
        					   r['sd_delivery']=Ext.Date.format(record.data.sd_delivery,'Y-m-d');
        				   var params=new Object();
        				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
        				   params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
    					   Ext.Ajax.request({
    					   	  url : basePath +'scm/sale/splitSale.action',
    					   	  params : params,
    					   	  waitMsg:'拆分中...',
    					   	  method : 'post',
    					   	  callback : function(options,success,response){
    					   		var localJson = new Ext.decode(response.responseText);
    					   		if(localJson.success){
    			    				saveSuccess(function(){
    			    					Ext.getCmp('sdqty').setValue(remainqty);
    			    					//add成功后刷新页面进入可编辑的页面 
    			    					me.loadSplitData(originaldetno,said,record);  
    			    				});
    				   			} else if(localJson.exceptionInfo){
    				   				var str = localJson.exceptionInfo;
    				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    				   					str = str.replace('AFTERSUCCESS', '');
    				   					saveSuccess(function(){
    				    					//add成功后刷新页面进入可编辑的页面 
    				   					 me.loadSplitData(originaldetno,said,record);  
    				    				});
    				   					showError(str);
    				   				} else {
    				   					showError(str);
    					   				return;
    				   				}
    					   			
    					   	 } else{
    				   				saveFailure();
    				   			}
    					   	  }
    					   });
    					   
    				   }
    				}
    			},{
    				xtype:'button',
    				columnWidth:0.1,
    				text:'关闭',
    				width:60,
    				iconCls: 'x-button-icon-close',
    				margin:'0 0 0 10',
    				handler:function(btn){
    					Ext.getCmp('win').close();
    				}
    			}]
    		},{
    		  xtype:'gridpanel',
    		  region:'south',
    		  id:'smallgrid',
    		  layout:'fit',
    		  height:'80%',
    		  columnLines:true,
    		  store:Ext.create('Ext.data.Store',{
					fields:[{name:'sd_delivery',type:'date'},{name:'sd_qty',type:'int'},{name:'sd_sendqty',type:'int'},{name:'sd_ysendnotify',type:'int'},{name:'sd_id',type:'int'},{name:'sd_pmcdate',type:'date'}],
				    data:[]
    		  }),
    		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    		        clicksToEdit: 1,
    		        listeners:{
    		        	'edit':function(editor,e,Opts){
    		        		var record=e.record;
    		        		var version=record.data.ma_version;
    		        		if(version){
    		        			e.record.reject();
    		        			Ext.Msg.alert('提示','不能修改已拆分明细!');
    		        		}
    		        	}
    		        }
    		    })],
    		  tbar: [{
    			    tooltip: '添加批次',
    	            iconCls: 'x-button-icon-add',
    	            width:25,
    	            handler : function() {
    	            	var store = Ext.getCmp('smallgrid').getStore();
    	                var r = new Object();
    	                r.sd_delivery=record.get('sd_delivery');
    	                r.sd_pmcdate=record.get('sd_pmcdate');
    	                r.sd_qty=0; 
    	                r.sd_id=0;
    	                r.sd_detno=store.getCount()+1;
    	                store.insert(store.getCount(), r);
    	            }
    	        }, {
    	            tooltip: '删除批次',
    	            width:25,
    	            itemId: 'delete',
    	            iconCls: 'x-button-icon-delete',
    	            handler: function(btn) {
    	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
    	                var record=sm.getSelection();
    	                var sd_id=record[0].data.sd_id;
    	                if(sd_id&&sd_id!=0){
    	                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
    	                	return;
    	                }
    	                var store=Ext.getCmp('smallgrid').getStore();
    	                store.remove(record);
    	                if (store.getCount() > 0) {
    	                    sm.select(0);
    	                }
    	            },
    	            disabled: true
    	        }],
    	      listeners:{
    	    	  itemmousedown:function(selmodel, record){
    	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
    	    	  },
    	    	  afterrender : function(grid) {
    	    		  me.BaseUtil.getSetting('Sale', 'sd_delivery', function(bool) {
	       				if(bool) {
	       					grid.down('gridcolumn[dataIndex=sd_delivery]').hide();
	       				}
	       	          });
	       	    	  me.BaseUtil.getSetting('Sale', 'sd_pmcdate', function(bool) {
	       				if(bool) {
	       					grid.down('gridcolumn[dataIndex=sd_pmcdate]').hide();
	       				}
	       	          });
   			   	  }
    	      }, 
    		  columns:[{
    			 dataIndex:'sd_detno',
    			 header:'序号',
    			 format:'0',
    			 xtype:'numbercolumn'
    		   },{
    			  dataIndex:'sd_delivery',
    			  header:'交货日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  }
    		  },{
    			  dataIndex:'sd_pmcdate',
    			  header:'PMC回复日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  }
    		  },{
    			  dataIndex:'sd_qty',
    			  header:'数量',
    			  width:120,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				   return val;
   			     },
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    			dataIndex:'sd_yqty',
    			header:'已转发货数',
    			xtype:'numbercolumn',
    			width:100,
    			editable:false
    		  },{
    			 dataIndex:'sd_sendqty',
      			header:'已转通知数',
      			xtype:'numbercolumn',
      			width:100,
      			editable:false  
    		  },{
    			  dataIndex:'sd_id',
    			  header:'sdid',
    			  width:0,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  }]
    		}]
    		
    	}).show();
         this.loadSplitData(originaldetno,said,record); 
	},
	loadSplitData:function(detno,said,record){
		 var grid=Ext.getCmp('smallgrid');
         grid.setLoading(true);//loading...
 		Ext.Ajax.request({//拿到grid的columns
         	url : basePath + "common/loadNewGridStore.action",
         	params:{
         	  caller:'SaleSplit',
         	  condition:"sd_detno="+detno+" AND sd_said="+said+" order by sd_id asc"
         	},
         	method : 'post',
         	callback : function(options,success,response){
         		grid.setLoading(false);
         		var res = new Ext.decode(response.responseText);
         		if(res.exceptionInfo){
         			showError(res.exceptionInfo);return;
         		}
         		var data = res.data;
         		if(!data || data.length == 0){
         			grid.store.removeAll();
         			var o=new Object();
         			o.sd_detno=detno;
         			o.sd_delivery=record.data.sd_delivery;
         			o.sd_pmcdate=record.data.sd_pmcdate;
         			o.sd_qty=record.data.sd_qty;
         			o.sd_yqty=record.data.sd_yqty;
         			o.sd_sendqty=record.data.sd_sendqty;
         			o.sd_id=record.data.sd_id;
         			data.push(o);
         		}
         		 grid.store.loadData(data);
         	}
         });
	},
	
	loadFitting : function (pr_code,sd_qty,sa_id,detno){
		Ext.Ajax.request({
	   		url : basePath + 'scm/sale/getFittingData.action',	   
	   		params: {
	   			caller: caller,
	   			pr_code: pr_code,
	   			qty:sd_qty,
	   			sa_id:sa_id,
	   			detno:detno	   			
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				Ext.Msg.alert("提示", "载入配件成功!", function() {
										var g = Ext.getCmp('grid');
										g.GridUtil.loadNewStore(g, {
												    caller : caller,
													condition : 'sd_said ='+ sa_id
												});
									});
	   			}
	   		}
		});											 
	},
	getPmcWindow : function() {
		var me = this;
		return Ext.create('Ext.window.Window',{
			width: 330,
	       	height: 180,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>更改PMC交期</h1>',
	       	layout: {
	       		type: 'vbox'
	       	},
	       	items:[{
	        	 width:'100%',
	        	 html: '<div style="background:transparent;border:none;width:100%;height:30px;' + 
	        	 	'color:#036;vertical-align:middle;line-height:30px;font-size:14px;">' + 
	        	 	'*注:修改订单交期请制作销售变更单<a style="float:right" href="javascript:' + 
	        	 	'openTable(\'变更交期\',\'jsps/scm/sale/saleChange.jsp?whoami=SaleChange\',\'SaleChange\');">进入</a></div>'
	         },{
	        	 margin: '5 0 0 5',
	       		 xtype:'datefield',
	       		 fieldLabel:'PMC回复交期',
	       	     name:'pmcdate',
	       	     format:'Y-m-d',
	       	     id:'pmcdate'
	       	 },{
	       		margin: '5 0 0 5',
	                xtype: 'fieldcontainer',
	                fieldLabel: '全部更新',
	                combineErrors: false,
	                defaults: {
	                    hideLabel: true
	                },
	                layout: {
	                    type: 'column',
	                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
	                },
	                items: [{
	                    xtype:'checkbox',
	                    columnidth: 0.4,
	                    fieldLabel:'全部更新',
	                    name:'allupdate',
	                    id:'allupdate'
	           	 },{
	           		 xtype:'displayfield',
	           		 fieldStyle:'color:red',
	           		 columnidth: 0.6,
	           		 value:'  *更改当前所有明细'
	           	 }]
	         }],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'保存',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.savePmc(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
	},
	savePmc: function(w) {
		var pmcdate = w.down('field[name=pmcdate]').getValue(),
			grid = Ext.getCmp('grid'),
			record = grid.getSelectionModel().getLastSelected(); 
		if(!pmcdate && !delivery) {
			showError('请先设置PMC回复日期') ;  
			return;
		} else if(Ext.Date.format(pmcdate, 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')){
			showError('PMC回复日期小于系统当前日期') ;  
			return;
		} else {
			var allupdate = w.down('field[name=allupdate]').getValue();
			var dd = {
					sd_id : record.data.sd_id,
					sd_said : record.data.sd_said,
					pmcdate : pmcdate ? Ext.Date.format(pmcdate,'Y-m-d') : null,
					allupdate : allupdate ? 1 : 0
			};
			Ext.Ajax.request({
				url : basePath +'scm/sale/updatepmc.action',
				params : {
					_noc: 1,
					data: unescape(Ext.JSON.encode(dd))
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.success){
	    				showMessage('提示', '更新成功!', 1000);
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else{
		   				saveFailure();
		   			}
				}
			});
		}
	},
	UpdatePmc:function(record){
		var win = this.pmcwindow;
		if (!win) {
			win = this.pmcwindow = this.getPmcWindow();
		}
		win.show();
	},
});
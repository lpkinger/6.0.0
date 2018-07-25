Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.stepio', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
      		'core.form.Panel','pm.make.Stepio','core.toolbar.Toolbar','pm.make.StepioMakeScrapGrid','core.trigger.MultiDbfindTrigger',
      		'core.button.Save','core.button.Update','core.button.Delete','core.button.Add','core.button.Submit','core.button.ResSubmit'
      		,'core.button.Print','core.button.ResAudit','core.form.MultiField','core.button.Post', 'core.button.ResPost',
  			'core.button.Audit','core.button.Close','core.grid.Panel2','pm.mes.DisplayPanel','core.button.Print','core.button.PrintByCondition',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
  	],
	init:function(){
		var me = this;
		this.control({
			'#setclash':{
				click:function(){
					var si_id=Ext.getCmp('si_id').value;
					warnMsg('是否重新设置冲减单据和数量', function(btn){
						if(btn == 'yes'){
							Ext.Ajax.request({//拿到grid的columns
					        	url : basePath + 'pm/make/setclash.action',
					        	params: {
					        		id:si_id
					        	},
					        	async:  false ,
					        	method : 'post',
					        	callback : function(options,success,response){
					        		var res = new Ext.decode(response.responseText);
					        		if(res.success){
					        			window.location.reload();
					        		} 
					        	}
							});
						}
					});
				}
			},
			'erpPostButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('si_statuscode');
                    if (status && status.value != 'COMMITED') {
                        btn.hide();
                    }
                },
                click: function(){
                	 me.FormUtil.onPost(Ext.getCmp('si_id').value);
                }
			},
			'erpResPostButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('si_statuscode');
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: function(){
                	 me.FormUtil.onResPost(Ext.getCmp('si_id').value);
                }
			},
			'erpFormPanel':{
				afterrender:function(){
					var formCondition = getUrlParam('formCondition');//从url解析参数
					if(formCondition!=null){
						me.getClashqty(formCondition.replace(/IS/g,"="));
					} 
				}
			},
			'erpGridPanel2': {
				itemclick: function(selModel, record) {
					 this.GridUtil.onGridItemClick(selModel, record);
				}				
			},
			'stepiomakescrapgrid': {
				itemclick: function(selModel, record) {
					 this.GridUtil.onGridItemClick(selModel, record);
				}				
			},
			'multidbfindtrigger[name=md_mmcode]':{
				  beforetrigger: function(field) {
						var macode=Ext.getCmp('si_makecode');
	                    if (macode.value !=null && macode.value != '') {
	                        field.dbBaseCondition = "ma_code='" + macode.value+"'";
	                    }
	                }
			},
			'multidbfindtrigger[name=md_mmdetno]':{
				  beforetrigger: function(field) {
						var macode=Ext.getCmp('si_makecode'); 
	                    if (macode.value !=null && macode.value != '' && macode.value != 0) {
	                        field.dbBaseCondition = "mm_code='" + macode.value+"'";
	                        var mmlevel=Ext.getCmp('mmlevel');
	                        var bomid=Ext.getCmp('mm_bomid');
	                        if(mmlevel && bomid && mmlevel.value !=null && mmlevel.value != '' && bomid.value > 0) {
	                        	 field.dbBaseCondition += " and mmlevel >'" + mmlevel.value+"' and topbomid="+ bomid.value ;
	                        }
	                    }
	                }
			},
			'erpSaveButton': {
				click: function(btn){
					if(Ext.getCmp('si_qty').value==null||Ext.getCmp('si_qty').value==0){
						showError("转移数量不能为0！");
    					return;
					}
					var st_inqty = Number(Ext.getCmp('st_inqty').value);
					var si_qty = Number(Ext.getCmp('si_qty').value);
					var st_ngoutqty = Number(Ext.getCmp('st_ngoutqty').value);
					if(si_qty+st_ngoutqty>st_inqty){
						showError("转移数量+不良数量不能大于投入数量！");
    					return;
					}
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					if(caller=='Stepio!CraftScrap'){
						this.beforeSave(this);			
					}else{
						this.FormUtil.beforeSave(this);					
					}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('si_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					if(caller=='Stepio!CraftScrap'){
						this.onUpdate(this);			
					}else{
						this.FormUtil.onUpdate(this);		
					}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('Stepio', '新增工序单', 'jsps/pm/make/Stepio.jsp?whoami='+caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('si_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('si_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('si_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('si_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('si_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('si_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('si_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('si_id').value);
				}
			},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('si_id').value);
				}
			},
			'dbfindtrigger[name=si_mmdetno]': {
				afterrender:function(trigger){
	    			trigger.dbKey='si_makecode';
	    			trigger.mappingKey='ma_code';
	    			trigger.dbMessage='请先选择关联订单号！';
				}
    		},
    		'dbfindtrigger[name=st_inno]':{
    			afterrender:function(trigger){
	    			trigger.dbKey='si_makecode';
	    			trigger.mappingKey='mc_makecode';
	    			trigger.dbMessage='请先选择转出工单号！';
				}
    		},
    		'dbfindtrigger[name=si_prodcode]':{
    			beforetrigger:function(f){
    				var makecode=Ext.getCmp('si_makecode');  
    				if(caller='Stepio!CraftScrap' && makecode && makecode.value!=''){
    					f.findConfig =" mm_code='"+makecode.value+"'";
    				}
				}
    		},
    		'#si_lossrate':{
    			afterrender:function(f){ 
    				f.setValue(me.lossrateText);
    			}
    		}
		});
	}, 
	onUpdate:function(me, ignoreWarn, opts, extra){
		var me=this;
		var form = Ext.getCmp('form');
		var mm = me.FormUtil;		
		if(! mm.checkForm()){
			return;
		}
		var grid = Ext.getCmp('grid');
		var scrapgrid = Ext.getCmp('stepiomakescrapgrid');
		var param = me.GridUtil.getGridStore(grid);
		var param1 = me.GridUtil.getGridStore(scrapgrid);
		param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		var bool=true;
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = (opts && opts.dirtyOnly) ? form.getForm().getValues(false, true) : 
				form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(f && opts && opts.dirtyOnly) {
					extra = (extra || '') + 
					'\n(' + f.fieldLabel + ') old: ' + f.originalValue + ' new: ' + r[k];
				}				
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
				
				
			});
			if(opts && opts.dirtyOnly && form.keyField) {
				r[form.keyField] = form.down("#" + form.keyField).getValue();
			}
			if(!mm.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}  
			var errInfo='<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>',
			errInfo1='',errInfo2='';
			if(grid.columns.length > 0 && !grid.ignore){
				if(grid.GridUtil.isDirty(grid)) {
					if(grid.necessaryField && grid.necessaryField.length > 0 && (param == null || param.length == 0 || param == '' || param == '[]') && !ignoreWarn){
						errInfo1= me.GridUtil.getUnFinish(grid);																
					} 
				}else if (me.GridUtil.isDirty(scrapgrid)){
					if(scrapgrid.necessaryField && scrapgrid.necessaryField.length > 0 && (param1 == null || param1.length == 0 || param1 == '' || param1 == '[]') && !ignoreWarn){
						errInfo2= me.GridUtil.getUnFinish(scrapgrid);				
					}				
				}else {
					mm.update(r, param,param1, extra);
				}
				if(errInfo1.length > 0 || errInfo2.length > 0){
					errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo1+'<hr>'+errInfo2+'</div>';
				warnMsg(errInfo, function(btn){
					if(errInfo1.length==0){
						param='';
					}
					if(errInfo2.length==0){
						param1='';
					}
					if(btn == 'yes' || btn == 'ok'){
						mm.update(r, param,param1, extra);
					}else{
						return;
					}
				});
				}else {
					mm.update(r, param,param1, extra);
				};	
			} else {
				mm.update(r, param,param1, extra);
			}
		}else{
			mm.checkForm(form);
		}		
	},
	beforeSave:function(me, ignoreWarn, opts, extra){ 
		var me=this;
		var mm = me.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
			if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
				mm.getSeqId(form);
			}
		}
		var grid = Ext.getCmp('grid'); 
		var removea = new Array(); 
		Ext.each(removea,function(r,index){
			Ext.Array.remove(grids,r);
		});  
		var param = me.GridUtil.getGridStore();
		if(grid.necessaryField&&grid.necessaryField.length > 0 && (param == null || param == '')){
				var errInfo = me.GridUtil.getUnFinish(grid);
				if(errInfo.length > 0)
					errInfo = '<div style="margin-left:50px">明细表有必填字段未完成填写, 继续将不会保存未完成的数据，是否继续?<hr>' + errInfo+'</div>';
				else
					errInfo = '明细表还未添加数据, 是否继续?';
				warnMsg(errInfo, function(btn){
					if(btn == 'yes'){
						mm.onSave(param, arg);
					} else {
						return;
					}
				});
		} else {
			mm.onSave(param, arg);
		} 
	
	},
	getClashqty:function(con){
		var me=this;   
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'pm/make/getClashInfo.action?caller='+caller,
        	params: {
        		con:con
        	},
        	async:  false ,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.success){
        			Ext.getCmp('mconmake').setValue(res.info.mconmake);
        			Ext.getCmp('mcremain').setValue(res.info.mcremain);
        			Ext.getCmp('clashqty').setValue(res.info.clashqty);
        			if(res.info.setclash==1){
        				Ext.getCmp('setclash').hidden=false;
        				Ext.getCmp('clashqty').setReadOnly(true);
        			}
        			if(res.info.saveclash==1){
        				Ext.getCmp('saveclash').hidden=false; 
            			Ext.getCmp('clashqty').setMaxValue(res.info.mcremain);
        			}
        			if(res.info.lossrateText){
        				if(Ext.getCmp('si_lossrate')){
        					Ext.getCmp('si_lossrate').setValue(res.info.lossrateText);
        				}
        				me.lossrateText=res.info.lossrateText;
        			}
        			if(res.info.notclash && res.info.notclash=='Y'){
        				var	clashgrid=Ext.ComponentQuery.query('erpDisplayGridPanel');	
        				if(clashgrid){
        					clashgrid[0].hide();
        				}
        				Ext.getCmp('grid').anchor='100% 65%';
        			}else{
        				Ext.getCmp('grid').anchor='100% 35%';
        			}
        		} 
        	}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
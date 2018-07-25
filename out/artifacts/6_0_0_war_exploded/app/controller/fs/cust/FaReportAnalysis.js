Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.FaReportAnalysis', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.FaReportAnalysis', 'core.grid.Panel2',
			'core.button.Save', 'core.button.Upload','core.button.Close','core.button.Delete',
			'core.button.Update','core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField'],
	init : function() {
		var me = this;
		this.control({
			'field[name=ra_id]': {
				afterrender:function(field){
					if(formCondition){
						var id = formCondition.substring(formCondition.indexOf('=')+1);
						field.setValue(id);
					}
				}
			},
			'erpFormPanel' : {
    			afterload : function(form) {
    				var me = this;
    				Ext.defer(function(){
    					var cuname = getUrlParam('cuname');
        				me.loadFaitems(cuname);
        				me.loadCreditTargetsItems(cuname);
					}, 200);
				}
    		},
        	'field[name=ra_instructions]': {
    			beforerender : function(f) {
    				f.emptyText = '是否规范、合理、可信，是否审计';
				}
    		},
    		'field[name=ra_cateremark]': {
    			beforerender : function(f) {
    				f.emptyText = '主要科目及变化分析';
				}
    		},
    		'field[name=ra_abilityremark]': {
    			beforerender : function(f) {
    				f.emptyText = '盈利能力、偿债能力、营运能力，及成长能力分析';
				}
    		},
    		'erpSaveButton': {
    			afterrender:function(btn){
					if(readOnly==1){
						btn.hide();
					}
				},
    			click: function(btn){
    				this.FormUtil.beforeSave(this);			
    			}
        	}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	loadFaitems: function(cuname){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: {
        		caller: 'RA_Faitems',
    			condition: "fi_cuname='" + cuname + "'"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        						fi_year : d.fi_year,
        						fi_num23 : d.fi_num23,
        						fi_num16 : d.fi_num16,
        						fi_num20 : d.fi_num20,
        						fi_num21 : d.fi_num21,
        						fi_num26 : d.fi_num26,
        						fi_num36 : d.fi_num36,
        						fi_num34 : d.fi_num34,
        						fi_num1 : d.fi_num1,
        						fi_num13 : d.fi_num13,
        						fi_cuname : d.fi_cuname
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('ra_faitems').store.loadData(fpd);
        		}
        	}
		});
	},
	loadCreditTargetsItems: function(cuname){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/loadNewGridStore.action",
        	params: {
        		caller: 'RA_CreditTargetsItems',
    			condition: "CTI_CUNAME='" + cuname + "'"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        						cti_year : d.cti_year,
        						cti_num1 : d.cti_num1,
        						cti_num3 : d.cti_num3,
        						cti_num4 : d.cti_num4,
        						cti_num12 : d.cti_num12,
        						cti_num28 : d.cti_num28,
        						cti_num14 : d.cti_num14,
        						cti_num8 : d.cti_num8,
        						cti_num11 : d.cti_num11,
        						cti_num13 : d.cti_num13,
        						cti_num17 : d.cti_num17,
        						cti_num19 : d.cti_num19,
        						cti_num20 : d.cti_num20,
        						cti_cuname : d.cti_cuname
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('ra_credittargetsitems').store.loadData(fpd);
        		}
        	}
		});
	}
});
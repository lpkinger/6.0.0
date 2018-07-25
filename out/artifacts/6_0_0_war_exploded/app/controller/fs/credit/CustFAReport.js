Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.CustFAReport', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fs.credit.CustFAReport', 'core.form.Panel', 'core.grid.Panel2', 'core.toolbar.Toolbar', 
	          'core.button.Scan', 'core.button.Export', 'core.button.Save', 'core.button.Update', 'core.button.Add',  'core.button.Upload', 
	          	'core.button.Close','core.button.DeleteDetail', 'core.button.Delete', 'core.form.MonthDateField', 'core.button.Measure', 
	          'core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger','core.form.YnField' ],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					this.FormUtil.beforeSave(this);
				}
			},
			'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);	
    			}
    		},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cr_id').value);			
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustFAReportImport', '新增客户财务报表导入', 'jsps/fs/credit/custFAReport.jsp');
				}
			},
			'erpMeasureButton' : {
				click: function(btn){
    				this.count();
    			}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
    		'combo[name=cr_fatype]': {
    			beforerender: function(field){
    				Ext.defer(function(){
    					if(Ext.getCmp('cr_id')&&Ext.getCmp('cr_id').value){
    						field.readOnly=true;
    					}
    					if(Ext.getCmp('grid').getStore().getCount() == 0){
    	    				if(!Ext.isEmpty(field.value)){
    	    					me.loadTemplet(field.value);
    	    				}
    					}
					}, 200);
				},
				change:function(field){
					var grid = Ext.getCmp('grid');
					grid.getStore().removeAll();
					if(Ext.isEmpty(field.value)){
    					showError('请选择报表类型');return;
    				} else {
    					me.loadTemplet(field.value);
    				}
				}
    		}
		});
	},
	loadTemplet: function(fsname){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/getFieldsDatas.action",
        	params: {
    			caller: 'FAREPORTTEMP left join FaReportTemplet on ft_id=fd_ftid',
				fields: 'fd_detno,fd_name,fd_step,fd_rightname,fd_rightstep,fd_fsname',
				condition: "fd_fsname='" + fsname + "' and ft_kind='保理客户' order by FD_DETNO"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = new Ext.decode(res.data);
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        						crd_detno : d.FD_DETNO,
        						crd_name : d.FD_NAME,
        						crd_step : d.FD_STEP,
        						crd_rightname : d.FD_RIGHTNAME,
        						crd_rightstep : d.FD_RIGHTSTEP,
        						crd_fsname : d.FD_FSNAME
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        		} else {
        			showError('没有可载入的'+fsname+'模版');return;
        		}
        	}
		});
	},
	count : function() {
		Ext.Ajax.request({
			url : basePath + "fs/credit/custFaReport/count.action",
			params:{
					cr_id: Ext.getCmp('cr_id').value,
					caller: caller
			},
			method:'post',
			callback:function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				attendDataComSuccess(function(){
    					window.location.reload();
    				});
    			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage('提示', str);
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
			}
		});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});
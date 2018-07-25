Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.ReportYearBegin', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.ReportYearBegin','core.grid.Panel2','core.toolbar.Toolbar','core.form.YearDateField','core.form.MonthDateField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpGridPanel2': { 
    			afterrender: function(g) {
    				setTimeout(function(){
	    				if(g.getStore().getCount() == 0){
	    					me.loadTemplet();
    					}
    				},500);
    			},
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				me.FormUtil.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addReportYearBegin', '新增合并报表数据导入', 'jsps/fa/gla/reportYearBegin.jsp?whoami=' + caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'field[name=yb_year]': {
    			afterrender: function(f) {
    				if (Ext.getCmp('yb_kind').value='利润表') {
    					me.getCurrentMonth(f);
    				}
    			}
    		},
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCurrentMonth: function(f) {
	    Ext.Ajax.request({
	    	url: basePath + 'fa/getMonth.action',
	    	params: {
	    		type: 'MONTH-A'
	    	},
	    	callback: function(opt, s, r) {
	    		var rs = Ext.decode(r.responseText);
	    		if(rs.data) {
	    			f.setValue(rs.data.PD_DETNO);
	    		}
	    	}
	    });
	},
	loadTemplet: function(){
		var ftname = Ext.getCmp('yb_kind').value;
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + 'common/getFieldsDatas.action',
        	params: {
        		caller: 'CONSOLIDATEDCOP_VIEW,FaReportTemp,FaReportTemplet',
				fields: 'row_number() over (order by ss_detno,fd_detno) ss_detno,ss_mastercode,ss_mastername,ss_name,fd_standard,fd_rightstandard,ft_name',
				condition: "ft_id=fd_ftid and FT_KIND='合并报表' and ft_name='" + ftname + "' order by ss_detno,fd_detno"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(r.exceptionInfo);return;
        		}
        		var data = new Ext.decode(res.data);
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        						ybd_detno : d.SS_DETNO,
        						ybd_mastercode : d.SS_MASTERCODE,
        						ybd_mastername : d.SS_MASTERNAME,
        						ybd_name : d.SS_NAME,
        						ybd_item: d.FD_STANDARD,
        						ybd_rightitem: d.FD_RIGHTSTANDARD,
        						ybd_kind: d.FT_NAME
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        		} else {
        			showError('没有可载入的集团公司');return;
        		}
        	}
		});
	}
});
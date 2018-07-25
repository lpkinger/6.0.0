Ext.QuickTips.init();
Ext.define('erp.controller.hr.attendance.EmpWorkdateQuery', {
	extend : 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil'],
	views : [ 'hr.attendance.EmpWorkdateQuery', 'hr.attendance.EwGridPanel', 'common.query.Form', 'core.trigger.DbfindTrigger',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField','common.datalist.Toolbar',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 'core.form.YearDateField',
			'core.form.ConMonthDateField','core.button.Refresh' ],
	refs : [ {
		ref : 'grid',
		selector : '#grid'
	} ],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
				'erpEwGridPanel': {
					afterrender: function(g) {
						var date=new Date;
				 		var year=date.getFullYear(); 
						var month=date.getMonth()+1;
					    month =(month<10 ? "0"+month:month); 
				        var mydate = (year.toString()+month.toString());
				        var temp = new Date(year,month,0);
				        var c=temp.getDate();
				        if(c==28){
				        	g.columns[32].hide();
				        	g.columns[33].hide();
				        	g.columns[34].hide();				        	      	
				        }
						if(c==29){
							g.columns[32].show();
							g.columns[33].hide();
				        	g.columns[34].hide();
				        }
						if(c==30){
							g.columns[32].show();
							g.columns[33].show();
				        	g.columns[34].hide();
				        }
				        }
					},
			'button[name=refresh]':{
    			click: function(btn){   
    				var form = me.getForm(btn);
   					form.onQuery();
       			}
			}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
 });
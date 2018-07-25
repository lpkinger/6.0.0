Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.Launch', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'erp.view.hr.kpi.Launch','common.batchDeal.Form','common.batchDeal.GridPanel','core.trigger.AddDbfindTrigger','core.form.ConDateHourMinuteField',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','erp.view.core.form.YearDateField','core.button.TurnMeetingButton',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','erp.view.core.form.QuarterField'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'condatefield[name=kd_time1]':{
    			afterrender:function(f){
    			f.combo.setValue(4);
    	    	f.setDateFieldValue(4);
    			}
    		},
    		'combo[name=kd_startkind]':{
    			 afterrender : function(c) {
    				 Ext.getCmp('kd_time1').hide();
    				 Ext.getCmp('kd_time2').hide();
    				 Ext.getCmp('kd_time3').hide();
    				 if (c.getValue() == 'season'){//||c.getValue() == 'PROCEDURE') {
    					 Ext.getCmp('kd_time1').hide();
        				 Ext.getCmp('kd_time2').show();
        				 Ext.getCmp('kd_time3').hide();
    				 }else if(c.getValue() == 'month'){
    					 Ext.getCmp('kd_time1').hide();
        				 Ext.getCmp('kd_time2').hide();
        				 Ext.getCmp('kd_time3').show();
    				 }else if(c.getValue() == 'manual'){
    					 Ext.getCmp('kd_time1').show();
        				 Ext.getCmp('kd_time2').hide();
        				 Ext.getCmp('kd_time3').hide();
    				 }
    			 },
    			 select : function(c) {
    				 var type = c.getValue();
    				 if (type ==  'season') {
    					 Ext.getCmp('kd_time1').hide();
        				 Ext.getCmp('kd_time2').show();
        				 Ext.getCmp('kd_time3').hide();
    				 } else if(type=='month'){
    					 Ext.getCmp('kd_time1').hide();
        				 Ext.getCmp('kd_time2').hide();
        				 Ext.getCmp('kd_time3').show();
    				 }else if(type=='manual'){
    					 Ext.getCmp('kd_time1').show();
        				 Ext.getCmp('kd_time2').hide();
        				 Ext.getCmp('kd_time3').hide();
    				 }else{
    					 Ext.getCmp('kd_time1').hide();
        				 Ext.getCmp('kd_time2').hide();
        				 Ext.getCmp('kd_time3').hide(); 
    				 }
    				 c.ownerCt.onQuery();
    			 }
    		},
    		'monthdatefield[name=kd_time3]': {
    			afterrender:function(f){
    				f.setMaxValue(Ext.Date.format(new Date(), 'Ym')-1+'');
    				
    				if(Ext.Date.format(new Date(), 'Ym').substring(4,6)=='01'){
    					f.setValue(Ext.Date.format(new Date(), 'Ym').substring(0,4)-1+"12");
    				}else{
    					f.setValue(Number(Ext.Date.format(new Date(), 'Ym'))-1);
    				}
    			},
    			change: function(f) {
    				if(Ext.getCmp('kd_startkind').value=='month'){
    				}
    			}
			},
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var items = form.items.items, autoQuery = false;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(autoQuery) {
						setTimeout(function(){
							form.onQuery();
						}, 1000);
					}
    			}
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				/*var form = Ext.getCmp('dealform');
    				//me.resize(form, grid);
    				grid.store.on('datachanged', function(store){
						me.getProductWh(grid);
					});*/
    			}
    		},
    		'erpVastDealButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		}
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
				fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(35 + fh);
			grid.setHeight(height - fh - 35);
			this.resized = true;
		}
    },
    vastDeal: function(url){
    	var flag=false;
    	var t=Ext.Date.format(new Date(),'Ymd');//当前日期
    	var me = this, grid = Ext.getCmp('batchDealGridPanel');
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		item.index = this.data[grid.keyField];
        		grid.multiselected.push(item);        		
        	}
        });
    	var form = Ext.getCmp('dealform');
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length > 0){
			var params = new Object();
			params.id=new Array();
			params.caller = caller;
			var data = new Array();
			var bool = false;
			var time_from='' ,time_to='',period='';
			var type=Ext.getCmp('kd_startkind').value;
			if(type==''){
				showError("请选择考核类型");
			}else if(type=='season'){
				var a=Ext.getCmp('kd_time2_a').value;
				var b=Ext.getCmp('kd_time2_b').value;
				var time=a+'-'+b;
				if(b=='Q1'){
					time_from=a+'0101';
	    			time_to=a+'0331';
				}else if(b=='Q2'){
					time_from=a+'0401';
	    			time_to=a+'0630';
				}else if(b=='Q3'){
					time_from=a+'0701';
	    			time_to=a+'0930';
				}else if(b=='Q4'){
					time_from=a+'1001';
	    			time_to=a+'1231';
				}
    		    period=time;
				if(time_to<t){
					flag=true;
				}else{
					showError("季度考核最晚为上一季度，请重新选择考核区间");
				}
			}else if(type=='month'){
    			var time=Ext.getCmp('kd_time3').value;
    			var y=time.substring(0,4);
    			var m=time.substring(4, 6);
    			var temp = new Date(y,m,0);
    			time_from=time+"01";
    			time_to=time+temp.getDate();
    		    period=time;
    		    flag=true;
    		}else if(type=='week'){
    			var now = new Date();
    			var nowDayOfWeek = now.getDay();
    			var nowDay = now.getDate();
    			var nowMonth = now.getMonth();
    			var nowYear = now.getFullYear();
    			var a = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek-7);
    			var b = new Date(nowYear, nowMonth, nowDay + (6 - nowDayOfWeek)-7);
    			time_from=Ext.Date.format(a,'Ymd');
    			time_to=Ext.Date.format(b,'Ymd');
    		    period=time_from+'-'+time_to;
    		    flag=true;
    		}else if(type=='manual'){
    			var a=Ext.getCmp('kd_time1_from').value;
    			var b=Ext.getCmp('kd_time1_to').value;console.log(b);
    			var str=a.getFullYear()+""+("0" + (a.getMonth() + 1)).slice(-2);
    			var str1=b.getFullYear()+""+ ("0" + (b.getMonth() + 1)).slice(-2);
    			if(a.getDate()<10){
    				str+='0'+a.getDate();
    			}else{
    				str+=a.getDate();
    			}
    			if(b.getDate()<10){
    				str1+='0'+a.getDate();
    			}else{
    				str1+=b.getDate();
    			}
    			period=str+'-'+str1;
    			time_from=str;
    			time_to=str1;
    			console.log(t);
    			console.log(time_to);
    			if(time_from>time_to){
    				showError("考核期间设置错误");
    			}else{
    				if(time_to<t){
    					flag=true;
    				}else{
    					showError("考核期间不能晚于当前时间");
    				}
    			}
    		}
			if(flag==true){
				Ext.each(records, function(record, index){
					var f = form.fo_detailMainKeyField;
					if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
		        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
		        		||(f && this.data[f] != null && this.data[f] != ''
			        		&& this.data[f] != '0' && this.data[f] != 0)){
						bool = true;
						var o = new Object();
						if(grid.keyField){
							o[grid.keyField] = record.data[grid.keyField];
						} else {
							params.id[index] = record.data[form.fo_detailMainKeyField];
						}
						if(grid.toField){
							Ext.each(grid.toField, function(f, index){
								var v = Ext.getCmp(f).value;
								if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
									if(Ext.isDate(v)){
										v = Ext.Date.toString(v);
									}
									o[f] = v;
								} else {
									o[f] = '';
								}
							});
						}
						if(grid.necessaryFields){
							Ext.each(grid.necessaryFields, function(f, index){
								var v = record.data[f];
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								if(Ext.isNumber(v)){
									v = (v).toString();
								}
								o[f] = v;
							});
						}
						data.push(o);
					}
				});
				if(bool && !me.dealing){
					params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
					params.time_from=time_from;
					params.time_to=time_to;
					params.period=period;
					me.dealing = true;
					var main = parent.Ext.getCmp("content-panel");
					main.getActiveTab().setLoading(true);//loading...
					Ext.Ajax.request({
				   		url : basePath + url,
				   		params: params,
				   		method : 'post',
				   		timeout: 6000000,
				   		callback : function(options,success,response){
				   			main.getActiveTab().setLoading(false);
				   			me.dealing = false;
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
				   				var str = localJson.exceptionInfo;
				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
				   					str = str.replace('AFTERSUCCESS', '');
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery();
				   				}
				   				showError(str);return;
				   			}
			    			if(localJson.success){
			    				if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
			    				grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
				   				Ext.Msg.alert("提示", "处理成功!", function(){
				   					grid.multiselected = new Array();
				   					Ext.getCmp('dealform').onQuery();
				   				});
				   			}
				   		}
					});
				} else {
					showError("没有需要处理的数据!");
				}
			}
		} else {
			showError("请勾选需要的明细!");
		}
    },
	/**
	 * 加载系统所有账套
	 */
	getMasters: function(win){
		Ext.Ajax.request({
			url: basePath + 'common/getAbleMasters.action',
			method: 'get',
			callback: function(opt, s, res){
				var r = Ext.decode(res.responseText), c = r.currentMaster;
				if(r.masters){
					var form = win.down('form'), items = new Array();
    				for(var i in r.masters) {
    					var d = r.masters[i];
    					if(d.ma_name != c) {
    						if(d.ma_type == 3) {
    							var o = {boxLabel: d.ma_name + '(' + d.ma_function + ')', ma_name: d.ma_name};
            					items.push(o);
    						}
    					} else {
    						form.down('#ma_name').setValue(c);
    						form.down('#ma_function').setValue(d.ma_function);
    					}
    				}
    				form.add(items);
				}
			}
		});
	}      
});
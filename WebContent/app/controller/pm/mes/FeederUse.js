Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.FeederUse', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.FeederUse','core.form.Panel','common.query.GridPanel','common.datalist.GridPanel',
    			'common.datalist.Toolbar',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Close',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {   				
					var macode = Ext.getCmp('fu_makecode').value,linecode = Ext.getCmp('fu_linecode').value;
					if(Ext.isEmpty(macode))	{
						showError('请先选择制造单号!');
						return;
					}
					if(Ext.isEmpty(linecode))	{
						showError('请先选择线别!');
						return;
					}
			    	//适用Feeder规格列表
        			var querygrid = Ext.getCmp('querygrid');
    				var form = btn.ownerCt.ownerCt;
    				var urlcondition = querygrid.defaultCondition || '';
    				condition = me.spellCondition(form, urlcondition);
    				if(Ext.isEmpty(condition)) {
    					condition = querygrid.emptyCondition || '1=1';
    				}
    				me.beforeQuery(caller, condition);
    				var gridParam = {caller: caller, condition: condition, start: 1, end: getUrlParam('_end')||1000};
    				querygrid.GridUtil.loadNewStore(querygrid, gridParam);
    				//已领Feeder列表
        			var grid = Ext.getCmp('grid');
        			if(grid) {
        				var maid = Ext.getCmp('fu_maid').value, linecode = Ext.getCmp('fu_linecode').value, cond;
        				if(!Ext.isEmpty(linecode)){
        					cond = "fu_maid=" + maid +" and fu_linecode='" + linecode +"'";
        	    		} else {
        					cond += " and fu_maid=" + maid;
        				}
            			grid.formCondition = cond;
            			grid.getCount(null, grid.getCondition() || '');
        			}
    			}
    		},
    		'#feedercode': {
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
    						me.onConfirm();
        				}
    				}
    			}
    		},
			'#confirm' : {
				click: function(btn) {
					me.onConfirm();
				}
			},
			'#returnAll' : {
				click: function(btn) {
					var result = Ext.getCmp('t_result');
					var macode = Ext.getCmp('fu_makecode').value;
					if(Ext.isEmpty(macode))	{
						showError('请先选择制造单号!');
						return;
					}
					warnMsg("确定全部退回?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/returnAllFeeder.action',
    	    			   		params: {
    	    			   			makecode : Ext.getCmp('fu_makecode').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(response.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				result.append(r.exceptionInfo,'error');
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success){
    	    		    				result.append('\n全部退回成功!','success');
    	    		    				alert('全部退回成功!');
    	    		    				me.GridUtil.loadNewStore(form.ownerCt.down('querygrid'), {caller: caller, condition: "msl_makecode='" + Ext.Cmp('fu_makecode').value + "' and fu_linecode='" + Ext.Cmp('fu_linecode').value + "'"});
    	    		    				me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: "fu_makecode='" + Ext.Cmp('fu_makecode').value + "' and fu_linecode='" + Ext.Cmp('fu_linecode').value + "'"});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
				}
			},
			'tabpanel > #tab-list': {
				activate: function(panel) {
					if(panel.boxReady) {
						var grid = Ext.getCmp('grid');
	        			if(grid) {
	        				var macode = Ext.getCmp('fu_makecode').value, linecode = Ext.getCmp('fu_linecode').value, condition;
							if(!Ext.isEmpty(macode) && Ext.isEmpty(linecode)){
	        					condition = "fu_makecode='" + macode + "'";
	        	    		}
							if(!Ext.isEmpty(linecode) && Ext.isEmpty(macode)){
	        					condition = "fu_linecode='" + linecode + "'";
	        	    		}
							if(!Ext.isEmpty(linecode) && !Ext.isEmpty(macode)){
	        					condition = "fu_makecode='" + macode + "' and fu_linecode='" + linecode + "'";
	        	    		}
	            			grid.formCondition = condition;
	            			grid.getCount(null, grid.getCondition() || '');
	        			}
					} else {
						panel.boxReady = true;
						var macode = Ext.getCmp('fu_makecode').value, linecode = Ext.getCmp('fu_linecode').value, condition;
						if(!Ext.isEmpty(macode) && Ext.isEmpty(linecode)){
        					condition = "fu_makecode='" + macode + "'";
        	    		}
						if(!Ext.isEmpty(linecode) && Ext.isEmpty(macode)){
        					condition = "fu_linecode='" + linecode + "'";
        	    		}
						if(!Ext.isEmpty(linecode) && !Ext.isEmpty(macode)){
        					condition = "fu_makecode='" + macode + "' and fu_linecode='" + linecode + "'";
        	    		}
						panel.add({
							xtype: 'erpDatalistGridPanel',
							caller: 'FeederUse',
							anchor: '100% 100%',
							formCondition: condition
						});
					}
				}
			}
    	});
    },
    beforeQuery: function(call, cond) {    	
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: call,
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
		});
	},
	spellCondition: function(form, condition){
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else if(f.xtype == 'yeardatefield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				}else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						var _a = '';
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(_a == ''){
									_a += f.logic + "='" + d.data.value + "'";
								} else {
									_a += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += _a + ')';
					}
				}	else if(f.xtype=='adddbfindtrigger' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + ' in (' ;		
					} else {
						condition += ' AND ' + f.logic + ' in (';
					}
					var str=f.value,constr="";
					for(var i=0;i<str.split("#").length;i++){
						if(i<str.split("#").length-1){
							constr+="'"+str.split("#")[i]+"',";
						}else constr+="'"+str.split("#")[i]+"'";
					}
					condition +=constr+")";
				} else {
					if(f.value != null && f.value != ''){
						var val = String(f.value);
						if(contains(val, 'BETWEEN', true) && contains(val, 'AND', true)){
							if(condition == ''){
								condition += f.logic + " " + f.value;
							} else {
								condition += ' AND (' + f.logic + " " + f.value + ")";
							}
						} else if(f.logic == 'ym_view_param') {
							if(condition == ''){
								condition += " " + f.value;
							} else {
								condition += ' AND (' + f.value + ")";
							}
						} else if(contains(val, '||', true)){
							var str = '';
							Ext.each(f.value.split('||'), function(v){
								if(v != null && v != ''){
									if(str == ''){
										str += f.logic + "='" + v + "'";
									} else {
										str += ' OR ' + f.logic + "='" + v + "'";
									}
								}
							});
							if(condition == ''){
								condition += str;
							} else {
								condition += ' AND (' + str + ")";
							}
						} else {
							
							if(val.indexOf('%') >= 0) {
								if(condition == ''){
									condition += f.logic + " like '" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + " like '" + f.value + "')";
								}
							} else {
								if(f.logic=='CONDITION'){
									if(condition == ''){
										condition +=  f.value ;
									} else {
										condition += ' AND '  + f.value;
									}
								}else{
									if(condition == ''){
										condition += f.logic + "='" + f.value + "'";
									} else {
										condition += ' AND (' + f.logic + "='" + f.value + "')";
									}
								}
					
							}
						}
					}
				}
			}
		});
		return condition;
	},
	onConfirm : function(){
		var me = this, get = Ext.getCmp('get').value, back = Ext.getCmp('back').value;
		var result = Ext.getCmp('t_result'), feedercode = Ext.getCmp('feedercode').value,
			reason = Ext.getCmp('reason'), isuse = Ext.getCmp('isuse');
		var grid1 = Ext.getCmp('querygrid'), grid2 = Ext.getCmp('grid');
		var macode = Ext.getCmp('fu_makecode').value, maid = Ext.getCmp('fu_maid').value, linecode = Ext.getCmp('fu_linecode').value, condition1, condition2;		
		if(!Ext.isEmpty(maid) && Ext.isEmpty(maid)){
			condition1 = "mc_makecode='" + macode+"'";
			condition2 = "fu_maid=" + maid;
		}
		if(!Ext.isEmpty(linecode) && !Ext.isEmpty(maid)){
			condition1 = "mc_makecode='" + macode+"' and ps_linecode='" + linecode + "'";
			condition2 = "fu_maid=" + maid + " and fu_linecode='" + linecode + "'";
		} 
		if(Ext.isEmpty(feedercode)){
			showError('请先采集飞达编号！');
			return;
		}
		if(get){
			if(Ext.isEmpty(macode)){
			  showError('请先指定制造单号！');
			  return;
		    }
		    if(Ext.isEmpty(linecode)){
			  showError('请先指定线别！');
			  return;
		    } 
			me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'pm/mes/getFeeder.action',
		   		params: {
		   			feedercode: feedercode,
		   			makecode  : Ext.getCmp('fu_makecode').value,
		   			linecode  : Ext.getCmp('fu_linecode').value
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			me.FormUtil.getActiveTab().setLoading(false);
		   			var r = new Ext.decode(response.responseText);
		   			if(r.exceptionInfo){
		   				result.append(r.exceptionInfo, 'error');
		   			}
	    			if(r.success){
	    				result.append('飞达：' + feedercode + '，领用成功！');
	    				var gridParam = {caller: caller, condition: condition1, start: 1, end: getUrlParam('_end')||1000};
	    				grid1.GridUtil.loadNewStore(grid1, gridParam);
	    				if(grid2) {
		    				grid2.formCondition = condition2;
		    				grid2.getCount(null, grid2.getCondition() || '');
	    				}
		   			}
		   		}
			}); 
		} else if(back){ 
			if(isuse.value == 1){//选择是停用，则需要填写退回原因
				if(Ext.isEmpty(reason.value)){
					showError('请先填写退回原因！');
					return;
				}
			}
			me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'pm/mes/returnFeeder.action',
		   		params: {
		   			feedercode: feedercode,
		   			reason    : reason.value,
		   			isuse     : isuse.value
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			me.FormUtil.getActiveTab().setLoading(false);
		   			var r = new Ext.decode(response.responseText);
		   			if(r.exceptionInfo){
		   				result.append(r.exceptionInfo, 'error');
		   			}
	    			if(r.success){
	    				result.append('飞达：' + feedercode + '，退回成功！');
	    				var gridParam = {caller: caller, condition: condition1, start: 1, end: getUrlParam('_end')||1000};
	    				grid1.GridUtil.loadNewStore(grid1, gridParam);
	    				if(grid2) {
		    				grid2.formCondition = condition2;
		    				grid2.getCount(null, grid2.getCondition() || '');
	    				}
		   			}
		   		}
			});
		
		}
	}
});
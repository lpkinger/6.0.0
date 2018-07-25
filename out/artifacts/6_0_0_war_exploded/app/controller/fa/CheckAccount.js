Ext.QuickTips.init();
Ext.define('erp.controller.fa.CheckAccount', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil','erp.util.LinkUtil'],
    views: ['fa.CheckAccount','core.form.MonthDateField','core.grid.LinkColumn'],
    FormUtil: Ext.create('erp.util.FormUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
    LinkUtil: Ext.create('erp.util.LinkUtil'),
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=check]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.check(grid,btn);
    			}
    		},
    		'#allow' : {
    			change : function(f) {
    				if(!me.checked) {
    					if(f.getValue()) {
	    					Ext.getCmp('accoutover').setDisabled(false);
	    				} else {
	    					Ext.getCmp('accoutover').setDisabled(true);
	    				}
    				}
    			}
    		},
    		'button[id=accoutover]': {
    			click: function() {
    				this.startAccount();
    			}
    		},
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[id=resaccoutover]': {
    			click: function() {
    				var mod = module.substring(0,2);
			    	if(mod=='CP'){
				    	Ext.Msg.confirm('提示','反结账后当前账期'+me.currentMonth+'的数据将被清空，确定要继续吗？',function(option){
							if(option=='yes'){
				    			me.overAccount(mod);
			    			}
			    		});
			    	}else{
			    		me.overAccount(mod);
			    	}
		    	}
    		},
    		'grid[id=account-check]': {
    			afterrender:function(grid){
    				me.getCheckItems(grid);
    			},
    			itemclick: function(selModel, record) {
    				var val = record.get('check');
    				if(val == 'error') {
    					me.showDetail(selModel.ownerCt, record);
    				}
    			}
    		},
    		'#yearmonth': {
    			afterrender: function(f) {
    				this.getCurrentMonth(f);
    			},
    			change: function(f){
    				if(f.value&&module=='STF') {
	    				me.currentMonth = f.getValue();
	    				me.datestart = Ext.Date.format(Ext.Date.getFirstDateOfMonth(Ext.Date.parse(me.currentMonth, 'Ym')), 'Ymd');
	    				me.dateend = Ext.Date.format(Ext.Date.getLastDateOfMonth(Ext.Date.parse(me.currentMonth, 'Ym')), 'Ymd');
    					me.preMonth = Ext.Date.format(new Date(Ext.Date.getFirstDateOfMonth(Ext.Date.parse(me.currentMonth, 'Ym'))-24*60*60*1000) , 'Ym');
    				}
    			}
    		},
    		'#date': {
    			afterrender: function(f) {
    				me.getFreezeDetno(f);
    			}
    		}
    	});
    },
    getCurrentMonth: function(f) {
    	var me = this;
    	var types = {GL:'MONTH-A',CB:'MONTH-B',AR:'MONTH-C',AS:'MONTH-F',ST:'MONTH-P',CP:'MONTH-T',AP:'MONTH-V'};
    	var mod = module.substring(0,2);
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: types[mod]
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				me.currentMonth = rs.data.PD_DETNO;
    				me.datestart = Ext.Date.format(new Date(rs.data.PD_STARTDATE), 'Ymd');
    				me.dateend = Ext.Date.format(new Date(rs.data.PD_ENDDATE), 'Ymd');
    				if(module!='STF'){
    					f.setText(rs.data.PD_DETNO);	
    				}else{
	    				me.preMonth = Ext.Date.format(new Date(rs.data.PD_STARTDATE-24*60*60*1000) , 'Ym');
	    				f.setValue(me.currentMonth);
    				}
    			}
    		}
    	});
    },
    getCheckItems:function(grid){
    	var me = this;
		Ext.Ajax.request({
			url:basePath + 'fa/getCheckItems.action',
			params:{
				module:module,
				isCheck:true
			},
			method:'post',
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.data.length<1){
						grid.store.removeAll();
					}else{
						grid.store.loadData(res.data);
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});
    },
    onRefresh: function(isCheck){
		var me = this,bool = false;
		var params = new Object();
		params.module = module;
		
		if(module=='STF'){
			params.month=Ext.getCmp('yearmonth').value;
		}else{
			params.month=me.currentMonth;
		}
		
		Ext.Ajax.request({
			url : basePath + "fa/refreshEndData.action",
			params:params,
			method:'post',			
			async:false,
			timeout:1000000,
			callback:function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				if(isCheck){
    					bool = true;
    				}else{
    					showMessage('刷新完成！');
    				}
    			} else {
    				if(localJson.exceptionInfo){   	   				
    	   				showError(str);return;   	   				
    	   			}
    			}
			}
		});
		return bool;
	},
    startAccount: function(){
		var me = this,url='',params={};
		var mod = module.substring(0,2);
		
		if(module!='STF'){
			url = 'fa/startAccount.action';
			params = {
				yearmonth: me.currentMonth,
				module: mod,
				caller: caller
			};
		}else{
			url = 'scm/reserves/Periodsdetailfreeze.action';
			params = {
				pd_detno : Ext.getCmp('yearmonth').value,
				caller: caller
			}
		}
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params:params,
			timeout: 120000,
			method:'post',
			callback:function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				if(module=='STF'){
    					Ext.Msg.alert('提示','冻结成功！',function(){
    					window.location.reload();
    					});
    				}else{
    					endArSuccess(function(){
    						window.location.reload();
    					});
    				}
    			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage('提示', str);
    	   					window.location.reload();
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
			}
		});
	},
	overAccount: function(mod){
		var me = this,url='',params={};
		if(module!='STF'){
			url = 'fa/overAccount.action';
			params = {
				yearmonth: me.currentMonth,
				module: mod,
				caller: caller
			};
		}else{
			url = 'scm/reserves/Periodsdetailcancelfreeze.action';
			params = {
				caller: caller
			};
		}
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params:params,
			method:'post',
			callback:function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				if(module=='STF'){
    					Ext.Msg.alert('提示','反冻结成功！',function(){
    					window.location.reload();
    					});
    				}else{
    					unEndArSuccess(function(){
    						window.location.reload();
    					});
    				}
    			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage('提示', str);
    	   					window.location.reload();
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
			}
		});
	},
    check: function(grid,btn) {
  		var me =this; 
    	var wins = Ext.ComponentQuery.query('window');
    	var params = new Object();
    	params.module = module;
    	Ext.Array.each(wins,function(win){
    		win.close();
    	}); 
    	if(module=='STF'){
			params.yearmonth=Ext.getCmp('yearmonth').value;
		}
		me.FormUtil.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/checkAccounts.action',
    		params: params,
    		timeout:1000000,
    		callback: function(opt, s, r) {
    			me.FormUtil.setLoading(false);
    			var res = Ext.decode(r.responseText);
    			if(res.success){
    				if(res.checks){
    					var checks = Ext.getCmp('checks');
    					checks.setText('<b>检测项数：</b>'+res.checks);
    					checks.show();
    				}
    				if(res.OKS){
    					var oks = Ext.getCmp('oks');
    					oks.setText('<b>已通过检测项数：</b>'+res.OKS);
    					oks.show();
    				}
    				if(res.errors){
    					var errors = Ext.getCmp('errors');
    					errors.setText('<b>未通过检测项数：</b><font  color="#FF0000">'+res.errors+'</font>');
    					errors.show();
    				}
	    			if(res.oks.length == grid.store.data.length) {
	    				var ch = 0;
	    				var idx =0;
	    				grid.store.each(function(f){
	    					if(res.oks[idx]) {
				    			f.set('check', 'checked');
			    			} else {
			    				f.set('check', 'error');
			    			}
			    			this.commit();
	    					if(this.get('check') == 'checked') {
	    						ch ++;
	    					}
	    					idx++;
	    				});
	    				if(idx == ch) {
	    					me.checked = true;
	    					Ext.getCmp('accoutover').setDisabled(false);
	    				} else {
	    					me.checked = false;
	    					var check = Ext.getCmp('check');
	    					var position = check.getPosition();
	    					var accoutover = Ext.getCmp('accoutover');
	    					var position1 = accoutover.getPosition();
	    					var resaccoutover = Ext.getCmp('resaccoutover');
	    					var position2 = resaccoutover.getPosition();
	    					var close = Ext.getCmp('close');
	    					var position3 = close.getPosition();
	    					if(isAccount){
	    						Ext.getCmp('allow').show();
	    					}
	    					check.setPosition(position[0],0);
	    					accoutover.setPosition(position1[0],0);
	    					resaccoutover.setPosition(position2[0],0);
	    					close.setPosition(position3[0],0);
	    				}	    		
	    			}
	    		}else if(res.exceptionInfo){
    				var str = res.exceptionInfo;
    				showError(str);
    			}
    			btn.setDisabled(false);
    		}
    	});
    },
    showDetail: function(grid, record) {
    	var me = this, wid = 'win-' + record.get('code_'),win = Ext.getCmp(wid);
    	var types = {GL:'总账',CB:'票据资金',AR:'应收',AS:'固定资产',ST:'库存',CP:'成本',AP:'应付'},mod = module.substring(0,2);
    	var columns = new Array(),data = [],fields = {},width=0;
    	
       	Ext.Ajax.request({
			url:basePath + 'fa/getShowDetailGrid.action',
			params:{
				checkcode:record.get('code_')
			},
			method:'get',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					width = res.width;
					if(res.data.length>0){
						data = new Ext.decode(res.data);
					}
					if(res.fields.length>0){
						fields = new Ext.decode(res.fields);
					}
					if(res.columns.length>0){
						columns = res.columns;
						Ext.Array.each(columns,function(column){
							if(width<460){
								column.flex = column.width/width*10;
							}
							var renderName = column.renderer;
							if(contains(column.renderer, ':', true)){
				    			var args = new Array();
				    			Ext.each(column.renderer.split(':'), function(a, index){
				    				if(index == 0){
				    					renderName = a;
				    				} else {
				    					args.push(a);
				    				}
				    			});
				    			column.args = args;
				    		}
				    		
				    		if(renderName){
					    		if(renderName=='linkError'){
					    			column.xtype = 'linkcolumn'
					    			column.handler = me.LinkUtil['Handler'];
					    		}
					    		column.renderer = me[renderName];
				    		}
						});
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);
					return;
				}
			}
		});
		var store = Ext.create('Ext.data.Store',{
			fields:fields,
			data:data
		});
		if(width<460){
			width = 485;
		}else if(width>680){
			width = 680;
		}else{
			width = width+25;
		}
    	if(!win) {
    		win = Ext.create('Ext.Window', {
        		title: record.get('title_'),
        		id: wid,
        		width: width,
        		height: '80%',
        		layout: 'fit',
        		items: [{
        			xtype:'grid',
        			layout : 'fit',
		    		autoScroll : true,
		        	columnLines: true,
		        	plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
		        	store:store,
		        	columns: columns
        		}],
        		buttonAlign: 'center',
        		buttons: [{
        			text: $I18N.common.button.erpExportButton,
        			iconCls: 'x-button-icon-excel',
        	    	cls: 'x-btn-gray',
        	    	margin: '0 10 0 5',
        			handler: function(btn) {
        				var win = btn.ownerCt.ownerCt; 
        				me.exportGrid(win.down('gridpanel'),types[mod]+'期末结账检查错误输出('+win.title+')');
        			}
        		},{
        			text: $I18N.common.button.erpCloseButton,
        			iconCls: 'x-button-icon-close',
        			cls: 'x-btn-gray',
        			margin: '0 5 0 15',
        			handler: function(btn) {
        				btn.ownerCt.ownerCt.close();
        			}
        		}]
        	});
    	}
    	win.show();
    },
    exportGrid: function(grid, title){
		title = title + Ext.Date.format(new Date(), 'Y-m-d H:i:s');
		var columns = (grid.columns && grid.columns.length > 0) ? 
				grid.columns : grid.headerCt.getGridColumns(),
				cm = new Array(), datas = new Array(), gf = grid.store.groupField;
		Ext.Array.each(columns, function(c){
			if(c.dataIndex == gf || (!c.hidden && (c.width > 0 || c.flex > 0) && !c.isCheckerHd)) {
				if((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
					var items = (c.items && c.items.items) || c.columns;
					Ext.Array.each(items, function(item){
						if(!item.hidden)
							cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')) + '(' + item.text.replace(/<br>/g, '\n') + ')', 
								dataIndex: item.dataIndex, width: item.width, xtype: item.xtype, format: item.format, locked: item.locked, summary: item.summaryType == 'sum', group: item.dataIndex == gf});
					});
				} else {
					cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')), dataIndex: c.dataIndex, width: (c.dataIndex == gf ? 100 : c.width), xtype: c.xtype, format: c.format, locked: c.locked, summary: c.summaryType == 'sum', group: c.dataIndex == gf});
				}
			}
		});
		
		var store = grid.getView().getStore(),
			items = store.data.items;
		if(store.buffered) {
			items = store.prefetchData.items;
		}
		var numreg = /^(-?\d+)(\.\d+)?$/, datereg = /\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/,strreg = /["]/;
		var numreg1 = /^(-?)(\.\d+)$/;//小数点吞零
		var pf = function(c, dd, ss) {
			if(c.xtype == 'datecolumn'){
				c.format = c.format || 'Y-m-d';
				if(Ext.isDate(dd[c.dataIndex])){
					ss[c.dataIndex] = Ext.Date.format(dd[c.dataIndex], c.format);
				} else if(datereg.test(dd[c.dataIndex])) {
					ss[c.dataIndex] = Ext.Date.format(
							Ext.Date.parse(dd[c.dataIndex], 'Y-m-d H:i:s'), c.format);
				}else{
					ss[c.dataIndex] = dd[c.dataIndex] ?  String(dd[c.dataIndex]).replace(strreg,'') : '';
				}
			} else if(c.xtype == 'datetimecolumn'){
				if(Ext.isDate(dd[c.dataIndex])){
					ss[c.dataIndex] = Ext.Date.format(dd[c.dataIndex], 'Y-m-d H:i:s');
				} else if(datereg.test(dd[c.dataIndex])) {
					ss[c.dataIndex] = dd[c.dataIndex];
				}else{
					ss[c.dataIndex] = dd[c.dataIndex] ?  String(dd[c.dataIndex]).replace(strreg,'') : '';
				}
			} else if(c.xtype == 'numbercolumn'){
				if(Ext.isNumber(dd[c.dataIndex])){
					ss[c.dataIndex] = String(dd[c.dataIndex].toString());
				} else if(numreg.test(dd[c.dataIndex])){
					ss[c.dataIndex] = String(dd[c.dataIndex].toString());
				}else if(numreg1.test(dd[c.dataIndex])){
					ss[c.dataIndex] = String(dd[c.dataIndex].toString()).replace('.','0.');
				}else {
					ss[c.dataIndex] = '0';
				}
			} else if(c.xtype == 'yncolumn') {
				ss[c.dataIndex] = dd[c.dataIndex] == 0 ? '否' : '是';
			} else {				
				ss[c.dataIndex] = dd[c.dataIndex] ?  String(dd[c.dataIndex]).replace(strreg,'') : '';
			}
			if(ss[c.dataIndex] == null) {
				ss[c.dataIndex] = '';
			}
		};
		Ext.each(items, function(d){
			var ss = {};
			Ext.each(columns, function(c){
				if(c.dataIndex == gf || (!c.hidden && (c.width > 0 || c.flex>0)&& !c.isCheckerHd)) {
					if((c.items && c.items.length > 0) || (c.columns && c.columns.length > 0)) {
						var items = ( c.items && c.items.items) || c.columns;
						Ext.Array.each(items, function(item){
							if(!item.hidden) {
								pf(item, d.data, ss);
							}
						});
					} else {
						pf(c, d.data, ss);
					}
				}
			});
			datas.push(ss);
		});

		if (!Ext.fly('ext-grid-excel')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-grid-excel';  
			frm.name = frm.id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);  
		}  
		Ext.Ajax.request({
			url: basePath + 'common/excel/grid.xls',
			method: 'post',
			form: Ext.fly('ext-grid-excel'),
			isUpload: true,
			params: {
				datas: unescape(Ext.JSON.encode(datas).replace(/\\u/g,"%u")),
				columns: unescape(Ext.encode(cm).replace(/\\u/g,"%u")),
				title: title
			}
		});
	},
	linkError: function(val, meta, record, x, y, store, view) {
		var grid = view.ownerCt,column = grid.columns[y];
		if (['合并制作', '小计', '无', '空'].indexOf(val) > -1) {
			return val;
		}
		return column.defaultRenderer(val, meta, record);
		
	},
	getFreezeDetno: function(f) {
		Ext.Ajax.request({
			url: basePath + 'scm/reserve/getFreezeDetno.action',
			method: 'GET',
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.data) {
					f.setValue(rs.data);
				} else {
					f.setValue('无');
				}
			}
		});
	}
});
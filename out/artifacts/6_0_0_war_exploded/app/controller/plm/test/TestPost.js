Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.TestPost', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil','erp.util.BaseUtil'],
    views: ['plm.test.TestPost'],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
      	this.control({ 
      		'button': {
      			click: function(btn){
      				var cal = btn.caller;
      				switch(cal) {
	      				case 'ProdInOut!PurcCheckin':
	      					me.test(cal, btn.text,"采购验收单");
	      					break;
	      				case 'ProdInOut!PurcCheckout':
	      					me.test(cal, btn.text,"采购验退单");
	      					break;
	      				case 'ProdInOut!Sale':
	      					break;
	      				case 'ProdInOut!SaleReturn':
	      					break;
	      				case 'ProdInOut!Picking':
	      					break;
	      				case 'ProdInOut!Make!Return':
	      					break;
      				}
      			}
      		}
      	});
    },
    test: function(caller, title, piclass){
    	var me = this;
    	Ext.create('Ext.Window', {
    		id: 'test-win',
    		height: '100%',
    		width: '100%',
    		title: title,
    		layout: 'border',
    		caller: caller,
    		items: [{
    			region: 'center',
    			layout: 'anchor',
        		items: [{
        			xtype: 'form',
        			anchor: '100% 5%',
        			layout: 'hbox',
        			items: [{
            			fieldLabel: '压力指数',
            			xtype: 'combo',
            			editable: false,
            			store: Ext.create('Ext.data.Store', {
            				fields: ['display', 'value'],
            				data: [{display: 10, value: 10}, {display: 20, value: 20}, {display: 50, value: 50}, 
            				       {display: 100, value: 100}, {display: 200, value: 200}, {display: 500, value: 500},
            				       {display: 1000, value: 1000}]
            			}),
            			displayField: 'display',
            			valueField: 'value',
            			queryMode: 'local',
            			value: 10
            		},{
            			xtype: 'button',
            			text: '生成测试单据',
            			cls: 'x-btn-blue',
            			width: 100,
            			handler: function(btn){
            				me.createPreData(caller, btn.ownerCt.down('combo').value,piclass);
            			}
            		},{
            			xtype: 'button',
            			text: '过账',
            			cls: 'x-btn-blue',
            			width: 100,
            			handler: function(){
            				me.post();
            			}
            		},{
            			xtype: 'button',
            			text: '查看物料库存',
            			cls: 'x-btn-blue',
            			width: 100,
            			handler: function(){
            				me.wareHouse();
            			}
            		},{
            			xtype: 'button',
            			text: '清除测试数据',
            			cls: 'x-btn-blue',
            			width: 100,
            			handler: function(){
            				me.clear();
            			}
            		}]
        		},{
        			xtype: 'panel',
        			anchor: '100% 95%',
        			layout : 'fit',
        			html : '<iframe src="' + basePath + 'jsps/scm/reserve/prodInOut.jsp?whoami=' + caller + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
        		}]
    		},{
    			region: 'east',
    			width: '30%',
    			id: 'log',
    			xtype: 'panel',
    			title: '<div style="padding-top:5px;color:#FF6A6A;background: #E0EEEE url(' + basePath + 
					'resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif) repeat center center">&nbsp;测试记录</div>',
				layout: 'anchor',
				items: [{
					anchor: '100% 100%',
					xtype: 'textarea',
					value: ''
				}],
				append: function(val){
					var v = this.down('textarea').value || '';
					this.down('textarea').setValue(v + '\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s') 
							+ '\n>>>>' + val + '\n');
				},
				clear: function(){
					this.down('textarea').setValue('');
				}
    		}],
    		listeners: {
    			close: function(){
    				me.clear();
    			}
    		}
    	}).show();
    },
    createPreData: function(caller, count, piclass){
    	var me = this,w = Ext.getCmp('test-win');
    	w.down('#log').clear();
    	me.logger('正在生成采购单数据');
    	//生成采购
    	Ext.Ajax.request({
    		url: basePath + 'plm/test/initPurchase.action',
    		params: {
    			count: count
    		},
    		callback: function(opt, s, r){
    			var res = Ext.decode(r.responseText);
    			if(res.exceptionInfo) {
    				me.logger(res.exceptionInfo.replace(/<BR>|<br>|<br\/>/g, '\n'));return;
    			}
    			if(res.data) {
    				var c = Ext.decode(res.data)[0].pd_code;
    				me.logger('生成采购单,单号:' + c);
    				w.preData = c;
    				me.createTestData(caller, count, res.data, piclass);
    			}
    		}
    	});
    },
    createTestData: function(caller, count, data, piclass){
    	var me = this;
    	me.logger('正在生成出入库单数据');
    	var t1 = new Date().getTime();
    	var win = Ext.getCmp('test-win');
    	win.setLoading(true);
    	//生成采购
    	Ext.Ajax.request({
    		url: basePath + 'plm/test/initProdIOPurc.action',
    		params: {
    			data: data,
    			count: count,
    			piclass:piclass,
    			caller:caller
    		},
    		timeout: 100000,
    		callback: function(opt, s, r){
    			win.setLoading(false);
    			var res = Ext.decode(r.responseText);
    			if(res.exceptionInfo) {
    				me.logger(res.exceptionInfo.replace(/<BR>|<br>|<br\/>/g, '\n'));return;
    			}
    			if(res.data) {
    				me.logger("已生成" + count + "条出入库单据\n>>>>耗时:" + (new Date().getTime() - t1)/1000 + "秒");
    				win.testData = res.data;
    				var co = res.data[0];
    			    var iframe = win.getEl().select('iframe').elements[0];
    			    iframe.src = basePath + 'jsps/scm/reserve/prodInOut.jsp?whoami=' + caller + 
    			    	'&formCondition=pi_inoutnoIS\'' + co + '\'&gridCondition=pd_inoutnoIS\'' + co + '\'';
    			}
    		}
    	});
    },
    post: function(){
    	var w = Ext.getCmp('test-win'), me = this;
    	if(w) {
    		if(w.testData) {
    			var ok = 0,ng = 0, len = w.testData.length,t = new Date().getTime();
    			w.setLoading(true);
    			Ext.each(w.testData, function(c){
    				Ext.Ajax.request({
    					url: basePath + 'plm/test/postProdIOPurc.action',
    					params: {
    						code: c
    					},
    					timeout: 150000,
    					callback: function(opt, s, r) {
    						var res = Ext.decode(r.responseText);
    						if(res.result) {
    							me.logger('过账失败,单号:' + c + '\n原因:' + res.result);
    							ng++;
    						} else {
    							ok++;
    						}
    						if(ok + ng == len) {
    							w.setLoading(true);
    							me.logger('过账通过率:' + (100 * ok/len) + '%\n' + '>>>>过账失败率:' + (100 * ng/len) + '%\n' + 
    									'>>>>总用时:' + ((new Date().getTime() - t)/1000) + '秒');
    		    			    var iframe = w.getEl().select('iframe').elements[0];
    		    			    iframe.src = iframe.src;
    						}
    					}
    				});
    			});
    		}
    	}
    },
    wareHouse: function(){
    	var w = Ext.getCmp('test-win');
    	if(w) {
    		if(w.preData) {
    	    	var ww = Ext.create('Ext.Window', {
    	    		width: '70%',
    	    		height: '90%',
    	    		title: '物料库存查询',
    	    		layout: 'anchor',
    	    		items: [{
    	    			anchor: '100% 100%',
    	    			xtype: 'grid',
    	    			columnLines: true,
    	    			columns: [{
    	    				text: '物料编号',
    	    				width: 160,
    	    				dataIndex: 'PD_PRODCODE'
    	    			},{
    	    				text: '数量',
    	    				width: 400,
    	    				columns: [{
        	    				text: 'Productwh',
        	    				flex: 1,
        	    				xtype: 'numbercolumn',
        	    				dataIndex: 'PW_ONHAND'
        	    			},{
        	    				text: 'PurchaseDetail',
        	    				flex: 1,
        	    				dataIndex: 'PD_ACCEPTQTY'
        	    			},{
        	    				text: 'Batch',
        	    				flex: 1,
        	    				dataIndex: 'BA_REMAIN'
        	    			},{
        	    				text: 'ProdIoDetail',
        	    				flex: 1,
        	    				dataIndex: 'PD_INQTY'
        	    			}]
    	    			},{
    	    				width: 400,
    	    				renderer: function(val, meta, record){
    	    					return (record.get('PW_ONHAND') == record.get('PD_ACCEPTQTY') == record.get('BA_REMAIN')
    	    						== record.get('PD_INQTY')) ? "正常" : "数量有异";
    	    				}
    	    			}],
    	    			store: Ext.create('Ext.data.Store', {
    	    				fields: ['PD_PRODCODE', 'PW_ONHAND', 'PD_ACCEPTQTY', 'BA_REMAIN', 'PD_INQTY'],
    	    				data: []
    	    			})
    	    		}]
    	    	});
    	    	ww.show();
    	    	ww.setLoading(true);
    	    	Ext.Ajax.request({
    		   		url : basePath + 'common/getFieldsDatas.action',
    		   		params: {
    		   			caller: 'PurchaseDetail left join io_pdinqty_view ' +
    		   				'on PurchaseDetail.pd_prodcode=io_pdinqty_view.pd_prodcode ' + 
    		   				'left join io_pwonhand_view on PurchaseDetail.pd_prodcode=io_pwonhand_view.pw_prodcode ' +
    		   				'left join io_batch_remain_view on PurchaseDetail.pd_prodcode=io_batch_remain_view.ba_prodcode',
    		   			fields: 'PurchaseDetail.pd_prodcode pd_prodcode,pw_onhand,pd_inqty,ba_remain,pd_acceptqty',
    		   			condition: 'pd_code=\'' + w.preData + '\''
    		   		},
    		   		method : 'post',
    		   		callback : function(options,success,response){
    		   			ww.setLoading(false);
    		   			var localJson = new Ext.decode(response.responseText);
    		   			if(localJson.exceptionInfo){
    		   				showError(localJson.exceptionInfo);return;
    		   			}
    	    			if(localJson.success){
    	    				var data = localJson.data;
    	    				ww.down('grid').store.loadData(Ext.decode(data));
    		   			}
    		   		}
    			});
    		}
    	}
    },
    clear: function(){
    	var w = Ext.getCmp('test-win');
    	if(w) {
    		if(w.preData) {
    			var codes = new Array();
    			Ext.each(w.testData, function(){
    				codes.push('\'' + this + '\'');
    			});
    			w.setLoading(true);
    			Ext.Ajax.request({
    				url: basePath + 'plm/test/clearProdIOPurc.action',
    				params: {
    					code: w.preData,
    					codes: Ext.Array.concate(codes, ',')
    				},
    				callback: function(opt, s, r) {
    					w.setLoading(false);
    					var res = Ext.decode(r.responseText);
    					if(res.success) {
    						alert('测试数据已清除');
    						w.down('#log').clear();
    						w.preData = null;
    						w.testData = null;
    						var iframe = w.getEl().select('iframe').elements[0];
    	    			    iframe.src = basePath + 'jsps/scm/reserve/prodInOut.jsp?whoami=' + w.caller;
    					}
    				}
    			});
    		}
    	}
    },
    logger: function(msg){
    	Ext.getCmp('log').append(msg);
    }
});
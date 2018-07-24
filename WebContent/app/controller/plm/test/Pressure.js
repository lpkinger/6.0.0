Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.Pressure', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil','erp.util.BaseUtil'],
    stores: ['TreeStore'],
    views: ['plm.test.Pressure', 'common.main.TreePanel'],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
      	this.control({ 
      		'erpTreePanel': { 
    			itemmousedown: function(selModel, record){
    				this.loadTab(selModel, record);
    			}
      		},
      		'button[id=t_input]': {//生成测试数据
      			click: function(btn){ 
      				if(this.contentWindow == null){
      					showError("请先选择需要测试界面");return;
      				} else {
	      				var form = this.getTestForm();
	      				var grid = this.getTestGrid();
	      				var count = Ext.getCmp('t_count').value;//压力测试次数
	      				var r = Ext.getCmp('t_result');
	      				r.setValue(r.value + '\n*****测试数据加载开始******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
	      				me.formStore = new Array();
	      				me.gridStore = new Array();
	      				me.BaseUtil.getRandomNumber(form.tablename, null, 'codeString');//自动添加编号
	      				for(var i=1;i<=count;i++){
	      					if(form){
	      						me.loadTestFormData(form, i);//加载测试数据
	          				}
	          				if(grid){
	          					me.loadTestGridData(grid, i);
	          				}
	          				r.setValue(r.value + '\n单据' + i + '数据加载完毕!');
	      				}
	      				r.setValue(r.value + '\n*****测试数据加载结束******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
	      				r.getEl().scroll('b', 10*count);
	      				Ext.getCmp('t_level1').setValue('');
	      				Ext.getCmp('t_input').setDisabled(true);
	      				Ext.getCmp('t_save').setDisabled(false);
      				}
      			}
      		},
      		'button[id=t_save]': {//执行保存
      			click: function(btn){     				
      				var r = Ext.getCmp('t_result');
      				if(me.formStore == null || me.formStore.length == 0){
      					showError("请先生成测试数据");return;
      				}else{
	      		    	var count = me.formStore.length;
	      				r.setValue(r.value + '\n*****测试数据保存开始******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
	      				r.ids = new Array();//保存测试数据的id
	      				me.reCount = 0;
	      				Ext.getCmp('testpage').setLoading(true);
	      				for(var i=1;i<=count;i++){
	      					me.testSave(me.formStore[i-1], me.gridStore[i-1], i);
	      				}
	      				Ext.getCmp('t_submit').setDisabled(false);
	      				Ext.getCmp('t_save').setDisabled(true);
      				}
      			}
      		},
      		'button[id=t_submit]': {//执行提交
      			click: function(btn){
      				var r = Ext.getCmp('t_result');
      				if(r.ids == null || r.ids.length == 0){
      					showError("请先生成测试数据");return;
      				}else{
          				me.testSubmit();
      				}   
      				Ext.getCmp('t_audit').setDisabled(false);
      				Ext.getCmp('t_submit').setDisabled(true);
      			}
      		},
      		'button[id=t_audit]': {//执行审核
      			click: function(btn){
      				var r = Ext.getCmp('t_result');
      				if(r.ids == null || r.ids.length == 0){
      					showError("请先生成测试数据");return;
      				}else{
          				me.testAudit();
      				}   
      				if(form.tablename='ProdInOut'){
      					Ext.getCmp('t_post').setDisabled(false);
      				}   				
      				Ext.getCmp('t_audit').setDisabled(true);
      			}
      		},
      		'button[id=t_analyse]': {//分析测试结果
      			click: function(btn){    				
      				Ext.Ajax.request({
    			   		url : basePath + 'common/getFieldData.action',
    			   		params: {
    			   			caller: 'DetailGrid',
    			   			field: 'dg_table',
    			   			condition: "dg_caller='" + me.contentWindow.caller + "'"
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    		        			showError(localJson.exceptionInfo);return;
    		        		}
    		    			if(localJson.success){
    		    				me.analyse(localJson.data);
    			   			}
    			   		}
    				});	
      			}
      		},
      		'button[id=t_report]': {
      			click: function(btn){
      				//生成测试报告
      				
      			}
      		},
      		'button[id=t_clear]': {
      			click: function(btn){
      				var r = Ext.getCmp('t_result');
      				if(r.ids == null || r.ids.length == 0){
      					showError("请先生成测试数据");return;
      				} else{
      					//清除测试数据
          				me.testDelete();
          				Ext.getCmp('t_level1').setValue('※');	
          				Ext.getCmp('t_result').setValue('测试结果');	
          				Ext.getCmp('t_save').setDisabled(true);
          				Ext.getCmp('t_submit').setDisabled(true);
          				Ext.getCmp('t_audit').setDisabled(true);
          				Ext.getCmp('t_post').setDisabled(true);
          				Ext.getCmp('t_input').setDisabled(false);
      				}
      			}
      		},
      		'button[id=prev]': {
      			click: function(btn){
      				var r = Ext.getCmp('t_result');
      				var d = null;
      				Ext.each(r.ids, function(k, i){
      					if(k.onload){
      						if(i > 0){
      							k.onload = false;
      							d = r.ids[i-1];
      							if(i == 1){
      								btn.setDisabled(true);
      							}
      						}
      					}
      				});
      				if(d){
      					me.loadFormStore(d.id);
      					me.loadGridStore(d.id);
      					d.onload = true;
      					Ext.getCmp('next').setDisabled(false);
      				} else {
      					btn.setDisabled(true);
      				}
      			}
      		},
      		'button[id=next]': {
      			click: function(btn){
      				var r = Ext.getCmp('t_result');
      				var d = null;
      				Ext.each(r.ids, function(k, i){
      					if(k.onload){
      						if(i < r.ids.length-1){
      							k.onload = false;
      							d = r.ids[i+1];
      							if(i == r.ids.length - 2){
      								btn.setDisabled(true);
      							}
      						}
      					}
      				});
      				if(d){
      					me.loadFormStore(d.id);
      					me.loadGridStore(d.id);
      					d.onload = true;
      					Ext.getCmp('prev').setDisabled(false);
      				} else {
      					btn.setDisabled(true);
      				}
      			}
      		}
      	});
    },
    loadTab: function(selModel, record){
    	var me = this;
    	if (record.get('leaf')) {
    		if(record.data['showMode'] != 0){
    			showError("该页面禁止进行压力测试");return;
    		}
    		var url = record.data['url'];
    		if(contains(url, 'datalist.jsp', true) || contains(url, 'query.jsp', true) || 
    				contains(url, 'print.jsp', true) || contains(url, 'batchPrint.jsp', true) || 
    				contains(url, 'batchDeal.jsp', true) || contains(url, 'vastDatalist.jsp', true) || 
    				contains(url, 'gridpage.jsp', true)){//列表、查询、报表等页面禁止进行压力测试
    			showError("该页面禁止进行压力测试");return;
    		}
    		Ext.getCmp('t_page').setValue(record.data['text']);
    		Ext.get('iframe_test').dom.setAttribute('src', basePath + me.parseUrl(url));
    		this.contentWindow = Ext.getCmp('testpage').el.dom.getElementsByTagName('iframe')[0].contentWindow;
    		this.removeTestPageBtns();
    	} else {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
					var tree = Ext.getCmp('tree-panel');
		            tree.setLoading(true, tree.body);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'common/lazyTree.action',
			        	params: {
			        		parentId: record.data['id']
			        	},
			        	callback : function(options,success,response){
			        		tree.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
				}
			}
    	}

    }, 
    openTab : function (panel, id, url){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = Ext.getCmp("content-panel"); 
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p);
    	} 
    },
    parseUrl: function(url){
		if(contains(url, 'session:em_uu', true)){//对url中session值的处理
			url = url.replace(/session:em_uu/,em_uu);
		}
		if(contains(url, 'session:em_code', true)){//对url中em_code值的处理
			url = url.replace(/session:em_code/, "'" + em_code + "'");
		}
		if(contains(url, 'sysdate', true)){//对url中系统时间sysdate的处理
			url = url.replace(/sysdate/, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
		}
		if(contains(url, 'session:em_name', true)){
			url = url.replace(/session:em_name/,"'"+em_name+"'" );
		}
		return url;
    },
    getTestForm: function(){
    	var w = this.contentWindow || Ext.getCmp('testpage').el.dom.getElementsByTagName('iframe')[0].contentWindow;
    	this.contentWindow = w;
    	return w.Ext.getCmp('form');
    },
    getTestGrid: function(){
    	var w = this.contentWindow || Ext.getCmp('testpage').el.dom.getElementsByTagName('iframe')[0].contentWindow;
    	return w.Ext.getCmp('grid');
    },
    loadTestFormData: function(form, index){
    	var data = new Object();
    	var v;
    	Ext.each(form.items.items, function(item){
    		v = '' + index;
    		if(Ext.isEmpty(item.originalValue)){
    			if(item.dataIndex == form.codeField){
        			v = Ext.getCmp('codeString').value + '-' + index;//自动添加编号
        		}
    			if(item.xtype == 'datefield'){
    				v = Ext.Date.format(new Date(), 'Y-m-d');
        		} else if(item.xtype == 'datetimefield'){
        			v = Ext.Date.format(new Date(), 'Y-m-d H:i:s');
        		} else if(item.xtype == 'erpYnField'){
        			v = 0;
        		}
    		} else {
    			v = item.value;
    			if(item.xtype == 'datefield'){
    				v = Ext.Date.format(v, 'Y-m-d');
        		} else if(item.xtype == 'datetimefield'){
        			v = Ext.Date.format(v, 'Y-m-d H:i:s');
        		}
    		}
    		if(item.name == form.keyField){
     			v = 0;
     		} 
    		data[item.name] = v;
    	});
    	this.formStore.push(data);
    },
    loadTestGridData: function(grid, index){
    	var data = new Array();
    	var d,v;
    	for(var i=0;i<20;i++){
    		d = new Object();
    		Ext.each(grid.columns, function(c){
    			v = '' + index;
        		if(c.dataIndex == grid.detno){
        			v = index*10000 + (i+1);//约定detno公式
        		}
        		if(c.xtype == 'datecolumn'){
        			v = Ext.Date.format(new Date(), 'Y-m-d');
        		} else if(c.xtype == 'datetimefield'){
        			v = Ext.Date.format(new Date(), 'Y-m-d H:i:s');
        		}
        		d[c.dataIndex] = v;
        	});
    		data.push(d);
    	}
    	this.gridStore.push(data);
    },
    /**
     * 保存测试
     */
    testSave: function(formStore, gridStore, index){
    	var me = this;
    	var r = Ext.getCmp('t_result');
    	r.setValue(r.value + '\n单据' + index + '开始保存\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    	Ext.Ajax.request({
	   		url : basePath + 'common/saveCommon.action',
	   		params : {
	   			caller: this.contentWindow.caller,
	   			formStore: unescape(Ext.JSON.encode(formStore).replace(/\\/g,"%")),
	   			param: unescape(Ext.JSON.encode(gridStore).replace(/\\/g,"%"))
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				me.reCount++;
    				r.setValue(r.value + '\n单据' + index + '保存成功\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    				r.ids.push({
    					index: index,
    					id: localJson.id
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
	   					me.reCount++;
	   					r.setValue(r.value + '\n单据' + index + '保存成功\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
	   					r.ids.push({
	    					index: index,
	    					id: localJson.id
	    				});
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();
	   			}
    			if(me.reCount == me.formStore.length){
    				Ext.getCmp('testpage').setLoading(false);
    				r.setValue(r.value + '\n*****测试数据保存结束******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    				//显示保存的数据
    				var d = Ext.Object.getValues(r.ids)[0];
    				d.onload = true;
    				me.loadFormStore(d.id);
    				me.loadGridStore(d.id);
    			}
	   		}
		});
    },
    /**
     * 删除测试
     */
    testDelete: function(){
    	var me = this;
    	var r = Ext.getCmp('t_result');
		r.setValue(r.value + '\n*****测试数据删除开始******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    	var caller = this.contentWindow.caller;
		if(r.ids && r.ids.length > 0){
			var count = r.ids.length;
			var reCount = 0;
			Ext.getCmp('testpage').setLoading(true);
			Ext.each(r.ids, function(k){
				r.setValue(r.value + '\n单据' + k.index + '开始删除\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				Ext.Ajax.request({
			   		url : basePath + 'common/deleteCommon.action',
			   		params: {
			   			caller: caller,
			   			id: k.id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}
		    			if(localJson.success){
		    				reCount++;
		    				r.setValue(r.value + '\n单据' + k.index + '删除成功\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			   			} else {
			   				delFailure();
			   			}
		    			if(reCount == count){
		    				Ext.getCmp('testpage').setLoading(false);
		    				r.setValue(r.value + '\n*****测试数据删除结束******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
		    				r.ids = new Array();
		    				me.contentWindow.location.reload();//刷新
		    				me.formStore = new Array();
		      				me.gridStore = new Array();
		      				me.removeTestPageBtns();
		    			}
			   		}
				});	
			});
		}
    },
    /**
     * 提交测试
     */
    testSubmit: function(){
    	var me = this;
    	var r = Ext.getCmp('t_result');
		r.setValue(r.value + '\n*****测试数据提交开始******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    	var caller = this.contentWindow.caller;
		if(r.ids && r.ids.length > 0){
			var count = r.ids.length;
			var reCount = 0;
			Ext.getCmp('testpage').setLoading(true);
			Ext.each(r.ids, function(k){
				r.setValue(r.value + '\n单据' + k.index + '开始提交\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				Ext.Ajax.request({
			   		url : basePath + 'common/submitCommon.action',
			   		params: {
			   			caller: caller,
			   			id: k.id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}
		    			if(localJson.success){
		    				reCount++;
		    				r.setValue(r.value + '\n单据' + k.index + '提交成功\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			   			} 
		    			if(reCount == count){
		    				Ext.getCmp('testpage').setLoading(false);
		    				r.setValue(r.value + '\n*****测试数据提交结束******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
		    				//显示保存的数据
		    				var d = Ext.Object.getValues(r.ids)[0];
		    				d.onload = true;
		    				me.loadFormStore(d.id);
		    				me.loadGridStore(d.id);
		    				if(Ext.getCmp('testpage').down('#next')){
		    					Ext.getCmp('testpage').down('#next').setDisabled(false);
		    				}
		    			}
			   		}
				});	
			});
		}
    },
    /**
     * 审核测试
     */
    testAudit: function(){
    	var me = this;
    	var r = Ext.getCmp('t_result');
		r.setValue(r.value + '\n*****测试数据审核开始******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
    	var caller = this.contentWindow.caller;
		if(r.ids && r.ids.length > 0){
			var count = r.ids.length;
			var reCount = 0;
			Ext.getCmp('testpage').setLoading(true);
			Ext.each(r.ids, function(k){
				r.setValue(r.value + '\n单据' + k.index + '开始审核\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				Ext.Ajax.request({
			   		url : basePath + 'common/auditCommon.action',
			   		params: {
			   			caller: caller,
			   			id: k.id
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}
		    			if(localJson.success){
		    				reCount++;
		    				r.setValue(r.value + '\n单据' + k.index + '审核成功\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			   			} 
		    			if(reCount == count){
		    				Ext.getCmp('testpage').setLoading(false);
		    				r.setValue(r.value + '\n*****测试数据审核结束******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
		    				//显示保存的数据
		    				var d = Ext.Object.getValues(r.ids)[0];
		    				d.onload = true;
		    				me.loadFormStore(d.id);
		    				me.loadGridStore(d.id);
		    			}
			   		}
				});	
			});
		}
    },
    analyse: function(tablename){
    	var r = Ext.getCmp('t_result');
		r.setValue(r.value + '\n*****数据分析开始******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
		if(r.ids && r.ids.length > 0){
			var count = r.ids.length;
			var reCount = 0;
			var grid = this.getTestGrid();
			Ext.getCmp('testpage').setLoading(true);
			Ext.each(r.ids, function(k){
				r.setValue(r.value + '\n单据' + k.index + '开始检查数据\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
				Ext.Ajax.request({
			   		url : basePath + 'common/getFieldData.action',
			   		params: {
			   			caller: tablename,
			   			field: grid.detno,
			   			condition: grid.mainField + '=' + k.id +  
			   				" AND (" + grid.detno + '<' + (k.index*10000 + 1) + ' OR ' + grid.detno + '>' + (k.index*10000 + 20) + ")" 
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
		        			showError(localJson.exceptionInfo);return;
		        		}
		    			if(localJson.data == null){
		    				reCount++;
		    				r.setValue(r.value + '\n单据' + k.index + '数据无误\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			   			} else {
			   				reCount++;
		    				r.setValue(r.value + '\n单据' + k.index + '数据有误,行号:' + localJson.data + 
		    						'\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
			   			}
		    			if(reCount == count){
		    				Ext.getCmp('testpage').setLoading(false);
		    				r.setValue(r.value + '\n*****数据分析结束******\n' + '时间:' + Ext.Date.format(new Date(), 'Y-m-d H:i:s'));
		    			}
			   		}
				});	
			});
		}
    },
    loadFormStore: function(id){
    	var form = this.getTestForm();
    	this.FormUtil.loadNewStore(form, {caller: this.contentWindow.caller, condition: form.keyField + "=" + id});
    	if(!Ext.getCmp('testpage').down('#testbar')){
    		Ext.getCmp('testpage').addDocked({
    			docked: 'top',
    			xtype: 'toolbar',
    			id: 'testbar',
    			items: [{
    				text: '上一条',
    				id: 'prev',
    				iconCls: 'x-button-icon-up',
    		    	cls: 'x-btn-gray',
    		    	disabled: true
    			},{
    				text: '下一条',
    				id: 'next',
    				iconCls: 'x-button-icon-down',
    		    	cls: 'x-btn-gray'
    			}]
    		});
    	}
    },
    loadGridStore: function(id){
    	var grid = this.getTestGrid();
    	this.GridUtil.loadNewStore(grid, {caller: this.contentWindow.caller, condition: grid.mainField + "=" + id});
    },
    /**
     * 去掉测试页面的buttons
     */
    removeTestPageBtns: function(){
    	var me = this;
    	setTimeout(function(){
    		if(!me.contentWindow.Ext || !me.contentWindow.Ext.getCmp('form')){
    			me.removeTestPageBtns();
    		} else {
    			var form = me.getTestForm();
    			Ext.each(form.dockedItems.items, function(item){
    				if(item){
    					form.removeDocked(item, true);
    				}
    			});
    			if(form.dockedItems.items.length > 0){
    				me.removeTestPageBtns();
    			}
    		}
    	}, 1000);
    }
});
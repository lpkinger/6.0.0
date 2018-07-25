Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.AutoInquiry', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.AutoInquiry','scm.purchase.AutoInquiryGrid','core.grid.Panel2','core.form.FileField',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Close','core.button.Delete',  				
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.window.SelectInquiryDate'
      	],
    selectInquiryDate_rowIndex:0,
    selectInquiryDate_count:0,
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({     		
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			cellclick: function(selModel, td, cellIndex, record, tr, rowIndex, e, eOpts) {
    				//点击的是静态询价周期，打开窗口
    				if(selModel.getGridColumns()[cellIndex]){
	    				if(selModel.getGridColumns()[cellIndex].dataIndex=='pk_jtcycle'){
	    					//添加行和周期在变量中
	    					me.selectInquiryDate_rowIndex = rowIndex;
	    					me.selectInquiryDate_count = record.get('pk_jtcycle');
	    				}
    				}
    			}
    		},
    		'combo':{
    			focus:function(combo,the){
    				if(combo.name=='pk_jtcycle'){  //点击从表的静态询价周期触发
    					Ext.create('erp.view.core.window.SelectInquiryDate',{rowIndex:me.selectInquiryDate_rowIndex,count:me.selectInquiryDate_count});
    				}else if(combo.name=='ai_jtcycle'){    //点击主表的静态询价周期触发
    					Ext.create('erp.view.core.window.SelectInquiryDate',{count:Ext.getCmp('ai_jtcycle').value});
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}    			
    				this.FormUtil.beforeSave(this);    				
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeSave(true);				
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ai_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAutoInquiry', '新增自动询价方案', 'jsps/scm/purchase/autoinquiry.jsp');
    			}
    		},    		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },   
    onGridItemClick: function(selModel, record){//grid行选择
		var me = this;
		var grid = selModel.ownerCt;
		if(grid && !grid.readOnly && !grid.NoAdd){
//			var index = grid.store.indexOf(record);
//			if(index == grid.store.indexOf(grid.store.last())){
//				var detno = parseInt(record.data['index']);
//				var data = getEmptyData(detno+1);//就再加10行
//				grid.store.loadData(data,true);
//			}
			var btn = grid.down('#deletedetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('copydetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('pastedetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('updetail');
			if(btn)
				btn.setDisabled(false);
			btn = grid.down('downdetail');
			if(btn)
				btn.setDisabled(false);
			if(grid.down('tbtext[name=row]')){
				grid.down('tbtext[name=row]').setText(index+1);
			}
	    }
	},
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
			jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(s[i].dirty){
					Ext.each(grid.columns, function(c){
						if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		return jsonGridData;
	},
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.FormUtil.checkForm()){
			return;
		}
		if(isUpdate){
			var s1 = me.FormUtil.checkFormDirty(form);
			var s2 = '';
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
				Ext.each(grids, function(grid, index){
					if(me.GridUtil){
						var msg = me.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s2 = s2 + '<br/>' + msg;
						}
					}
				});
			}
			if(s1 == '' && (s2 == '' || s2 == '<br/>')){
				showError('还未添加或修改数据.');
				return;
			}
		}
		var sign = 0;
		var grid = Ext.getCmp('grid');	
		var param = new Array();
		if(grid){
			param = me.getGridStore(grid);
		}
		param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			me.save(r, param, isUpdate ,sign);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0],isUpdate = arguments[arguments.length-1];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		warnMsg('是否同步更新到物料资料', function(btn){
			if(btn == 'yes'){
				params.sign = 1;
			}
			Ext.Ajax.request({
		   		url : basePath + form.updateUrl,
		   		params : params,
		   		method : 'post',
		   		callback : function(options,success,response){	   			
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){
	    				saveSuccess(function(){
	    					window.location.reload();
	    				});
		   			} else if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
		   					str = str.replace('AFTERSUCCESS', '');
		   					saveSuccess(function(){
		   						window.location.reload();
		    				});
		   					showError(str);
		   				} else {
		   					showError(str);
			   				return;
		   				}
		   			} else{
		   				saveFailure();//@i18n/i18n.js
		   			}
		   		}
			});
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});
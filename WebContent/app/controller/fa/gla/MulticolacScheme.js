Ext.QuickTips.init();
autoArrange = false;
Ext.define('erp.controller.fa.gla.MulticolacScheme', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.MulticolacScheme','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField',
      		'core.button.Audit','core.button.ResAudit','core.button.Submit','core.button.ResSubmit','core.button.Close','core.button.Delete',
      		'core.button.Update','core.button.DeleteDetail','core.button.Add','core.button.Save','core.button.Post','core.button.ResPost',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.AutoArrange'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			beforereconfigure : function(grid,store, columns, oldStore, oldColumns) {						
					var assistant = Ext.getCmp('mas_assistant');
					if(assistant&&assistant.checked){
						assistant.setReadOnly(true);
						var setbylevel = Ext.getCmp('mas_setbylevel');
						if(setbylevel){
							setbylevel.setValue(false);
							setbylevel.setDisabled(true);
						}
						var level = Ext.getCmp('mas_level');
		   				if(level){
		   					level.setValue(null);
		   					level.setReadOnly(true);
		   					level.setFieldStyle('background:#e0e0e0;color:#515151;');
		   				}
						Ext.Array.each(columns,function(column){		   						   						
   							if(column.dataIndex=='masd_cacode'){
   								column.hidden =true;
   							}
   							if(column.dataIndex=='masd_assistant'){
   								column.hidden =false;
   								var assistanttype = Ext.getCmp('mas_assistanttype');
				    			if(assistanttype&&!Ext.isEmpty(assistanttype.value)){
				    				me.setAssKind(assistanttype.value,grid,column);
				    			}
   							}   				   						 					
		   				});
    				}else{
	   					Ext.Array.each(columns,function(column){		   					
   							if(column.dataIndex=='masd_cacode'){
   								column.hidden =false;
   							}
   							if(column.dataIndex=='masd_assistant'){
   								column.hidden =true;
   							}	
   						});
	   				}
	   				   	 
	   			},
    			itemclick: this.onGridItemClick,
    			beforeedit: function(editor, e, Object){
    				var field = editor.field;
    				if(field=='masd_assistant'){
    					var assistanttype = Ext.getCmp('mas_assistanttype');
		    			if(!assistanttype||Ext.isEmpty(assistanttype.value)){
		    				showError('科目设置了多辅助核算，请设置某一辅助核算作为多栏账的项目!');
		    				return false;
		    			}
    				}
    			}
    		},
    		'erpFormPanel checkbox[name=mas_setbylevel]':{
    			change:function(field, newValue, oldValue, eOpts){
    				if(newValue){
    					var assistant = Ext.getCmp('mas_assistant');
    					if(assistant){
    						assistant.hide();
    					}
    					
    					var assistanttype = Ext.getCmp('mas_assistanttype');
    					if(assistanttype){
    						assistanttype.hide();
    					}
    					
    					var catecode = Ext.ComponentQuery.query('gridcolumn[dataIndex=masd_cacode]');    					
    					if(catecode[0]){
    						catecode[0].show();
    					}
    					
    					var assistant = Ext.ComponentQuery.query('gridcolumn[dataIndex=masd_assistant]');
    					if(assistant[0]){
    						assistant[0].hide();
    					}
    				}else{
    					var assistant = Ext.getCmp('mas_assistant');
    					if(assistant.checked){
    						assistant.setValue(false);
    						assistant.show();
    					}
    					var level = Ext.getCmp('mas_level');
    					if(level){
    						level.setValue(null);
    					}
    				}
    			}
    		},
    		'erpFormPanel checkbox[name=mas_assistant]':{
    			afterrender:function(field){
    				var setbylevel = Ext.getCmp('mas_setbylevel');
					if(setbylevel&&setbylevel.checked||!field.checked){
						field.hide();
					}
    			},
    			change:function(field, newValue, oldValue, eOpts){
    				if(newValue){   					
    					var assistanttype = Ext.getCmp('mas_assistanttype');
    					if(assistanttype){
    						assistanttype.show();
    					}
    					var catecode = Ext.ComponentQuery.query('gridcolumn[dataIndex=masd_cacode]');    					
    					if(catecode[0]){
    						catecode[0].hide();
    					}
    					var assistant = Ext.ComponentQuery.query('gridcolumn[dataIndex=masd_assistant]');
    					if(assistant[0]){
    						assistant[0].show();
    					}
    				}else{   					
    					var assistanttype = Ext.getCmp('mas_assistanttype');
    					if(assistanttype){
    						assistanttype.setValue(null);
    						assistanttype.hide();
    					}
    					var catecode = Ext.ComponentQuery.query('gridcolumn[dataIndex=masd_cacode]');    					
    					if(catecode[0]){
    						catecode[0].show();
    					}
    					var assistant = Ext.ComponentQuery.query('gridcolumn[dataIndex=masd_assistant]');
    					if(assistant[0]){
    						assistant[0].hide();
    					}
    				}
    			}
    		},
    		'field[name=mas_assistanttype]':{
    			afterrender:function(field){
    				var assistant = Ext.getCmp('mas_assistant');
					if(assistant&&!assistant.checked){
						field.hide();
					}
					var Store = field.store;
					Store.on('datachanged',function(store, eOpts){
						if(store.getCount()>1&&!field.hidden){
							field.allowBlank=false;	
							field.focus();
							document.getElementById("mas_assistanttype").childNodes[0].style ='margin-right:5px;width:100px;color:#FF0000;';														
						}
					});
    			},
    			change:function(field, newValue, oldValue, eOpts){
    				var grid = Ext.getCmp('grid');
    				var columns = grid.columns;
    				var column ='';
    				Ext.Array.each(columns,function(col){
    					if(col.dataIndex=='masd_assistant'){
    						column = col;
    					}
    				});
    				me.setAssKind(newValue,grid,column);
    			}
    		},
    		'field[name=mas_level]':{
    			change:function(field, newValue, oldValue, eOpts){
    				var setbylevel = Ext.getCmp('mas_setbylevel');
					if(setbylevel&&!setbylevel.isDisabled()){
	    				if(newValue>0){   	
							setbylevel.setValue(true);
	    				}else{
	    					setbylevel.setValue(false);
	    				}
					}
    			}
    		},
    		'dbfindtrigger[name=mas_cacode]':{   
    			afterrender: function(t){
    				t.autoDbfind = false;
    			},
		   		aftertrigger: function(t,record,dbfinds){   	   		  
		   			var isleaf = record.data['ca_isleaf']; 
		   			var setbylevel = Ext.getCmp('mas_setbylevel');
		   			var assistant = Ext.getCmp('mas_assistant');
		   			var level = Ext.getCmp('mas_level');
		   			var assistanttype = Ext.getCmp('mas_assistanttype');
		   			var name = Ext.getCmp('mas_name');
		   			if(name){
		   				var caname = record.data['ca_name'];
		   				name.setValue(caname+'多栏明细账');
		   			}
		   			var grid = Ext.getCmp('grid');
		   			grid.store.removeAll();
	    			me.GridUtil.add10EmptyItems(grid, 40, false);
		   			if(isleaf==1){
		   				if(level){
		   					level.setValue(null);
		   					level.setReadOnly(true);
		   					level.setFieldStyle('background:#e0e0e0;color:#515151;');
		   				}
						if(setbylevel){
							setbylevel.setValue(false);
							setbylevel.setDisabled(true);
						} 						
    					if(assistant){
    						assistant.setValue(true);
    						assistant.setReadOnly(true);
    						assistant.show();
    					}
    					me.getComboData(record);
		   			}else{
		   				if(level){
		   					level.setReadOnly(false);
		   					level.setFieldStyle('background:#FFFAFA;color:#515151;');
		   					level.setMinValue(parseInt(record.data['ca_level'])+1);
		   					level.minText='级别必须大于主表科目级别!'
		   				}
		   				if(setbylevel){
							setbylevel.setDisabled(false);
						} 
						if(assistant){
							assistant.setValue(false);
    						assistant.hide();
    					}
    					
    					var assistanttype = Ext.getCmp('mas_assistanttype');
    					if(assistanttype){
    						assistanttype.allowBlank=true;			
							document.getElementById("mas_assistanttype").childNodes[0].style ='margin-right:5px;width:100px;color:#0A0A0A;';
    						assistanttype.setValue(null);
	    				}
		   			}
		   		}
		   	},
		   	'multidbfindtrigger[name=masd_cacode]':{
    			beforetrigger:function(trigger){
    				var setbylevel = Ext.getCmp('mas_setbylevel');
    				var level = Ext.getCmp('mas_level');
					if(setbylevel&&setbylevel.checked&&level&&!Ext.isEmpty(level.value)){
						trigger.dbBaseCondition = "ca_level="+level.value;
					}
    			}
		   	},
    		'erpSaveButton': {
    			click: function(btn){ 
    				var str = me.checkColname();
    				if(str){
    					warnMsg('明细行'+str+'的栏目名称重复,是否继续保存?', function(btn){
							if(btn == 'yes' || btn == 'ok'){
								me.FormUtil.beforeSave(me);
							}
						});
    				}else{
    					me.FormUtil.beforeSave(me);
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('mas_id').value)});
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mas_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(){
    				var str = me.checkColname();
    				if(str){
    					warnMsg('明细行'+str+'的栏目名称重复,是否继续保存?', function(btn){
							if(btn == 'yes' || btn == 'ok'){
								if(autoArrange){
		    						me.onUpdate(autoArrange);
			    				}else{
			    					me.FormUtil.onUpdate(me);
			    				}
							}
						});
    				}else{
    					if(autoArrange){
    						me.onUpdate(autoArrange);
	    				}else{
	    					me.FormUtil.onUpdate(me);
	    				}
    				}
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mas_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('mas_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mas_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mas_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mas_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('mas_id').value);
				}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mas_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('mas_id').value);
				}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMulticolacScheme', '新增多栏账方案', 'jsps/fa/gla/multicolacScheme.jsp');
    			}
    		},
    		'erpAutoArrangeButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mas_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.autoArrange();
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getComboData: function(record) {
		if(this._combodata == null) {
			this._combodata = {};
		}
		var assistanttype = Ext.getCmp('mas_assistanttype');
		if(assistanttype){
			var store = assistanttype.store;			
			if(this._combodata && !this._combodata['base']) {	
				var datas= store.data.items;
				var arr = new Array();	
				Ext.Array.each(datas,function(data){
					arr.push(data.data);
				})
				this._combodata['base'] = arr;
			}
		}
		var asstype = record.data['ca_asstype'].split('#');  
		if(!asstype[0]||asstype[0]==''){
			var assistanttype = Ext.getCmp('mas_assistanttype');
			if(assistanttype){
				assistanttype.allowBlank=true;			
				document.getElementById("mas_assistanttype").childNodes[0].style ='margin-right:5px;width:100px;color:#0A0A0A;';
				var store = assistanttype.store;				
				store.loadData(this._combodata['base']);
				assistanttype.setValue(null);
			}
			var assistant = Ext.getCmp('mas_assistant');
			if(assistant){
				assistant.setValue(false);				
			}
			return ;
		}
		
		var assistanttype = Ext.getCmp('mas_assistanttype');
		if(assistanttype){
			var store = assistanttype.store;
			var assname = record.data['ca_assname'].split('#');
			var cacode = record.data['ca_code'];
			if(asstype.length==1){
				assistanttype.allowBlank=true;			
				document.getElementById("mas_assistanttype").childNodes[0].style ='margin-right:5px;width:100px;color:#0A0A0A;';
				assistanttype.setValue(asstype[0]);
			}
			if(this._combodata && this._combodata[cacode]) {
				store.loadData(this._combodata[cacode]);				
				return;
			}
			var arr = new Array();
			for(var i in asstype) {
				arr.push({
					display: assname[i],
					value:asstype[i] 
				});
			}		
			store.loadData(arr);
			this._combodata[cacode] = arr;
		}
	},
	setAssKind:function(assistanttype,grid,column){	
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsData.action',
	   		async: false,
	   		params: {
	   			caller: 'AssKind',
	   			fields: 'ak_dbfind,ak_asscode,ak_assname',
	   			condition: "ak_code='" + assistanttype+"'"
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
    			if(localJson.success){
    				var data = localJson.data;
    				if(data&&data.hasOwnProperty('ak_asscode')){
    					column.dbfind = data.ak_dbfind+'|'+data.ak_asscode;
	    				grid.dbfinds =Ext.Array.merge(grid.dbfinds,[{dbGridField:data.ak_asscode,field:'masd_assistant'},{dbGridField:data.ak_assname,field:'masd_colname'}]);
    				}
	   			}
	   		}
		});
	},
	autoArrange: function(){	
		var me = this;
		var assistanttype = Ext.getCmp('mas_assistanttype');
		if((assistanttype&&!assistanttype.allowBlank)&&Ext.isEmpty(assistanttype.value)){
			Ext.Msg.alert('警告','科目设置了多辅助核算，请设置某一辅助核算作为多栏账的项目!');
			return;
		}
		var grid =  Ext.getCmp('grid');
		var form = Ext.getCmp('form');
		var values = form.getValues();
		if(values['mas_setbylevel']==1&&!values['mas_level']){
			showError("按级别设置多栏账必须设置级别！");
			return;
		}
		var formStore = Ext.JSON.encode(values);
		Ext.Ajax.request({
	   		url : basePath + 'fa/gla/autoArrange.action',
	   		params: {
	   			formStore: formStore,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
    			if(localJson.success){
    				var res = localJson.result;
    				if(values['mas_id']){
    					var store = grid.getStore();
	    				if(res.over){
	    					Ext.Msg.alert("提示","超过100行只显示100行");
	    				}
	    				if(res.data.length>0){
	    					store.loadData(res.data);
	    				}else{
	    					store.removeAll();
	    					me.GridUtil.add10EmptyItems(grid, 40, false);
	    				}
	    				autoArrange = true;
    				}else{
    					if(res.tip){
    						showMessage('提示',res.tip,5000);
    					}
    					var value =res.id;
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
    				}
	   			}
	   		}
		});
	},
	onUpdate: function(extra){
		var me = this;
		var form = Ext.getCmp('form');
		var grid = Ext.getCmp('grid');
		if(form.codeField && (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
			showError('编号不能为空.');
			return;
		}
		if(form && form.getForm().isValid()){
			//form里面数据
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
			if(!contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			var params = [];
			if(grid.columns.length > 0 && !grid.ignore){
				Ext.Array.each(grid.columns,function(column){
					if(column.dataIndex=='masd_cacode'&&!column.hidden){
						grid.necessaryField = 'masd_cacode';
					}
					if(column.dataIndex=='masd_assistant'&&!column.hidden){
						grid.necessaryField = 'masd_assistant';
					}
				});
				if(me.GridUtil.isEmpty(grid)) {
					warnMsg('明细还未录入数据,是否继续保存?', function(btn){
						if(btn == 'yes' || btn == 'ok'){
							me.update(r, '[]', extra);
						} else {
							return;
						}
					});
				}else {
					var param = me.GridUtil.getAllGridStore(grid);
					params = unescape("[" + param.toString() + "]");
					me.update(r, params, extra);
				}
			}
		}else{
			me.FormUtil.checkForm(form);
		}
	},
	update: function(){
		var me = this, params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, '-', true) && !contains(k,'-new',true)){
				delete r[k];
			}
		});
		params.formStore = unescape(escape(Ext.JSON.encode(r)));
		params.param = unescape(arguments[1].toString());
		for(var i=2; i<arguments.length; i++) {  //兼容多参数
			if (arguments[i] != null)
				params['param' + i] = unescape(arguments[i].toString());
		}
		var form = Ext.getCmp('form'), url = form.updateUrl;
		if(url.indexOf('caller=') == -1){
			url = url + "?caller=" + caller;
		}
		me.FormUtil.setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath + url,
			params: params,
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage('提示', '保存成功!', 1000);
					autoArrange = false;
					//update成功后刷新页面进入可编辑的页面
					var u = String(window.location.href);
					if (u.indexOf('formCondition') == -1) {
						var value = r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(me.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					} else {
						window.location.reload();
					}
				} else if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						//update成功后刷新页面进入可编辑的页面 
						var u = String(window.location.href);
						if (u.indexOf('formCondition') == -1) {
							var value = r[form.keyField];
							var formCondition = form.keyField + "IS" + value ;
							var gridCondition = '';
							var grid = Ext.getCmp('grid');
							if(grid && grid.mainField){
								gridCondition = grid.mainField + "IS" + value;
							}
							if(me.contains(window.location.href, '?', true)){
								window.location.href = window.location.href + '&formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							} else {
								window.location.href = window.location.href + '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
							}
						} else {
							window.location.reload();
						}
					}
					showError(str);return;
				} else {
					updateFailure();
				}
			}
		});
	},
	checkColname: function(){
		var me = this,str='';
		var data = me.getAllGridStore();
		for(var i=0;i<data.length-1;i++){
			for(var j=i+1;j<data.length;j++){
				if(data[i]['masd_colname']==data[j]['masd_colname']){
					str += '、序号<font color=green>'+data[i]['masd_detno']+'</font>&nbsp;和序号<font color=green>'+data[j]['masd_detno']+'</font>&nbsp;';
				}
			}
		}
		return str.substring(1);
	},
	getAllGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var assistant = Ext.getCmp('mas_assistant').value;
		if(assistant&&assistant==1){
			grid.necessaryField ='masd_assistant';
		}else{
			grid.necessaryField ='masd_cacode';
		}
		var me = this,GridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(data[grid.necessaryField] != null && data[grid.necessaryField] != ""){
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						data[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					Ext.each(grid.columns, function(c){
						if((!c.isCheckerHd)&&(c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
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
					if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
						dd[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					GridData.push(dd);
				}
			}
		}
		return GridData;
	}
});
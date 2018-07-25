/**
 * dbfind trigger
 * 支持带条件dbfind
 */
Ext.define('erp.view.core.trigger.DbfindTrigger', {
	extend: 'Ext.form.ComboBox',
	alias: 'widget.dbfindtrigger',
	triggerCls: 'x-form-search-trigger',
	autoDbfind: true,
	autoShowTriggerWin:true,
	isCommonChange:false,
	triggerName:null,
	lastTriggerValue:null,
	/*displayField: 'name',*/
	searchField:'',
	searchTable:'',
	searchFieldArray:'',
	configSearchCondition:'1=1',
	ignoreMonitorTab:false,
	ifsearch:true,
	searchTpl:true,
	canblur : true,
	defaultListConfig:{
		maxHeight:210,
		autoScroll:true,
		minWidth:530
	},
	isFast:false,
	lastQueryValue:'',
    minChars:1, // 设置用户输入字符多少时触发查询
    tpl: '',
    enableKeyEvents:true,
    //输入值之后进行模糊查询
    doQuery: function(queryString, forceAll, rawQuery) {
    	queryString = queryString || '';
    	var me = this;
    	me.fireEvent('beforetrigger', me);//同步触发放大镜前事件
    	if(me.lastQueryValue!=queryString){
    		var judge=me.judgeForm(me);
    		var newtpl=[];
    		var field=[];
    		var sfield='';
    		var dbfind='';
    		var name=me.name;
    		me.lastQueryValue=queryString;
    		var dbCaller=me.dbCaller|| (typeof caller === 'undefined' ? '' : caller);
    		if(dbCaller==''&&typeof(me.ownerCt)!== 'undefined'&&typeof(me.ownerCt.caller)!== 'undefined'){
    			dbCaller=me.ownerCt.caller;//FORM
    		}
    		if (!me.ownerCt || me.column) {//GRID
    			if(me.column.dataIndex == name){
    				dbfind = me.column.dbfind;
    				var config=dbfind.split("|");
    				dbCaller=config[0];
    				}
    			}
        	if(me.ifsearch){
        		 if(queryString.trim()==''){
        			 me.collapse( );
        		 }else{
        			 if((me.searchTable.trim()==""||me.searchField.trim()=="")&&!me.searchTpl){
        				 me.ifsearch=false;
        				 me.collapse( );
        			 }else{
        				 if(me.configSearchCondition!=null && me.configSearchCondition.trim()!=''){
        					 var res=me.getSearchData(me.searchTable,me.searchField,queryString,me.configSearchCondition,dbCaller,name,me,judge,me.searchTpl); 
        				 }else{
        					 var res=me.getSearchData(me.searchTable,me.searchField,queryString,me.getCondition('1=1'),dbCaller,name,me,judge,me.searchTpl);
        				 }
        				 if(res.tpl!=null && res.tpl.trim()!=""){
        					 //加载tpl模板
     						var data =Ext.decode(res.tpl.replace(/,}/g, '}').replace(/,]/g, ']'));
     						newtpl=data;
     						if(newtpl.length>0){
     							var span="";
     							var width=0;
     							for(var i=0;i<newtpl.length;i++){
     								if(i==0){
     									span=span+'<span style="width:'+newtpl[i].width+'px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;float:left;">{'+newtpl[i].field+'}</span>';//display:block;
     								}else{
     									span=span+'<span style="width:'+newtpl[i].width+'px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;float:left;margin-left:10px;">{'+newtpl[i].field+'}</span>';
     								}
     								if(newtpl[i].dbField=="true"){
     									me.searchFieldArray=newtpl[i].field;
     								}
     								width=width+newtpl[i].width+20;
     								field.push(newtpl[i].field);
     								sfield=sfield+newtpl[i].field+',';
     							}
     							me.defaultListConfig.minWidth=width+10;
     							me.defaultListConfig.maxHeight=210;
     							me.defaultListConfig.autoScroll=true;
     							me.searchField=sfield.substring(0,sfield.length-1);
     							me.tpl=Ext.create('Ext.XTemplate',
     						            '<ul style="padding-left: 0px;"><tpl for=".">',
     						            '<li role="option" class="x-boundlist-item" style="list-style:none;">' ,
     						            '<div style="height:20px;">',
     						            ''+span+'',
     						            '</li>',
     						        '</tpl></ul>'
     							);
     							me.store=Ext.create('Ext.data.Store', {
     							   fields: field,
     							   data : []
     							});
     						}else{
     					        me.ifsearch=false;
     						}
     					}
        				var scondition=res.searchCondition;
        				if(!me.configSearchCondition || me.configSearchCondition=='1=1'){
     					    if(scondition==null || scondition.trim()=='') scondition=null;    					     	
     					    me.configSearchCondition=me.getCondition(scondition);
     					}
     					if(res.searchtable!=null && res.searchtable.trim()!=''){
     						me.searchTable=res.searchtable;
     					}
     					if(res.data!=null && res.data.trim()!=''){
     						data =Ext.decode(res.data);
     	    				if(data.length>0){
     	    					me.store.loadData(data,false);
     	       				 	me.expand();
     	    				}else{
     	    					me.collapse( );
     	    				}
     					}else{
     						me.collapse( );
     					}
        			 }
        		 }
        	}else{
        		return true;
        	}
            return true;
    	}else{
    		return false;
    	}
    },
	initComponent: function() {
		var me = this;
		me.addEvents({
			aftertrigger: true,
			beforetrigger: true
		});
		if(!me.ownerCt) {
			Ext.defer(function(){
				me.getOwner();
			}, 50);
		}
		this.displayField='display';	   
		this.valueField='value';
		this.queryMode='local';
		if(me.clearable) {
			me.trigger2Cls = 'x-form-clear-trigger';
			if(!me.onTrigger2Click) {
				me.onTrigger2Click = function(){
					this.setValue(null);
				};
			}
		}
		if(me.value){
			me.store=Ext.create('Ext.data.Store', {
				   fields: ['display','value'],
				   data : [{
					   'display':me.value,
					   'value':me.value
				   }]
			});
		}
		me.displayTpl='<tpl for=".">' +
        '{[typeof values === "object" ?  values["' + me.displayField + '"]:values]}' +
        '<tpl if="xindex < xcount">' + me.delimiter + '</tpl>' +
       '</tpl>';
		me.callParent(arguments);
	},
	getOwner: function() {
		var me = this;
		if (me.el) {
			var gridEl = me.el.up('.x-grid');
			if (gridEl) {
				var grid = Ext.getCmp(gridEl.id);
				if (grid) {
					me.owner = grid;
					me.column = grid.down('gridcolumn[dataIndex=' + me.name + ']');
				}
			}
		}
	},
	listeners: {
		select:function(combo,records,eOpts){
			var con ="";
			var which = 'form';
			var cal = combo.dbCaller||caller;
			var key = combo.triggerName||combo.name;
			if (!combo.ownerCt || combo.column) {
				which = 'grid';
				dbfind = combo.column.dbfind;
				cal = dbfind.split('|')[0];
				Ext.each(records,function(data){
					con = !Ext.isEmpty(data.data[Ext.util.Format.lowercase(combo.searchFieldArray)]) ? (combo.searchFieldArray + " = '" + data.data[Ext.util.Format.lowercase(combo.searchFieldArray)].replace(/\'/g,"''")  + "'") : null;
				});
			}else{
				Ext.each(records,function(data){
					con = !Ext.isEmpty(data.data[Ext.util.Format.lowercase(combo.searchFieldArray)]) ? (combo.searchFieldArray + " = '" + data.data[Ext.util.Format.lowercase(combo.searchFieldArray)].replace(/\'/g,"''")  + "'") : null;
				});
			}
			combo.setSelectValue(which, cal, key, combo.getCondition(con)); //光标移开后自动dbfind
		},
		focus: function(f) {
			var trigger = this;
			trigger.lastTriggerValue=trigger.value;
			trigger.lastQueryValue=trigger.value;//focus时 给lastTriggerValue和lastQueryValue赋值，autoSetValue后将lastTriggerValue和lastQueryValue清空，解决明细放大镜第一行联想后其他行联想无效
			trigger.lastTriggerId = trigger.id;
			if (!trigger.ownerCt || trigger.column) {
				trigger.emptyText='选择或按F2键';
				trigger.applyEmptyText();
				if (!trigger.owner) {
					trigger.getOwner();
				}
				var owner = trigger.owner;
				if(owner){
					if (owner.editingPlugin && owner.editingPlugin.activeEditor.field.id == trigger.id) {
						trigger.record = owner.editingPlugin.activeRecord;
					}else if(owner.lockedGrid && owner.lockedGrid.editingPlugin.activeEditor.field.id == trigger.id){
						owner = owner.lockedGrid;
						trigger.record = owner.editingPlugin.activeRecord;
					}else{
						trigger.record = owner.selModel.lastSelected;
					}
					var index = trigger.owner.store.indexOf(trigger.record);
					if (index != null) {
						trigger.lastTriggerId = trigger.id + '---' + index;
					} else {
						trigger.lastTriggerId = null;
					}
				}
			}
		},
		blur: function(f) {
			if(f.canblur){
				this.triggerblur.apply(this, [f]);
			}
		},
		keydown:function(f,e){
			var me = this;
			if(e.keyCode==113){//F2键
	    		if (!me.ownerCt || me.column) {//GRID
	    				me.onTriggerClick();
				}
			}
		}
	},
	triggerblur: function(f) {
		if (!f.readOnly) {
			if(this.lastTriggerId){
				var which = 'form';
				var cal = this.dbCaller||caller;
				var key = this.triggerName||this.name;
				var con = !Ext.isEmpty(this.lastValue) ? (key + " like '" + this.lastValue.replace(/\'/g,"''")  + "%'") : null;
				var currrecord = null;
				if (!this.ownerCt || this.column) {
					which = 'grid';
					dbfind = this.column.dbfind;
					cal = dbfind.split('|')[0];
					con = con ? (dbfind.split('|')[1] + " like '" + this.lastValue.replace(/\'/g,"''")  + "%'") : null;
					currrecord = this.owner.selModel.lastSelected;
					if (this.lastValue != null && this.lastValue != '') {
						var record = this.owner.store.getAt(this.lastTriggerId.split('---')[1]);
						this.owner.selModel.select(record);
					}
				}
				if((which=='form' && this.lastTriggerValue==this.lastValue) || (which=='grid' && this.record && (this.lastTriggerValue ==this.record.get(this.name) && this.lastTriggerValue==this.value )))
					return;
				if(!this.autoDbfind && f.lastValue){
					f.setValue(f.lastValue);
				}
				if (this.lastValue != null && this.lastValue != '' && this.lastTriggerId && !this.readOnly && this.autoDbfind) {
					if(false === f.fireEvent('beforetrigger', f))
						return;
					this.autoDbfind(which, cal, key, this.getCondition(con)); //光标移开后自动dbfind
					if (currrecord) {
						this.owner.selModel.select(currrecord);
					}
				}
			}			
			if(!this.lastValue && (this.dbfinds || (this.owner && this.owner.dbfinds))){
				var dbfinds = this.dbfinds || this.owner.dbfinds;
				this.resetDbfindValue(dbfinds);
			}
		}
	},
	onTriggerClick: function() {
		var trigger = this,
		bool = true; // 放大镜所在
		bool = trigger.fireEvent('beforetrigger', trigger);
		dbCaller=this.dbCaller|| (typeof caller === 'undefined' ? '' : caller);
		if(dbCaller==''&&typeof(trigger.ownerCt)!== 'undefined'&&typeof(trigger.ownerCt.caller)!== 'undefined'){
			dbCaller=trigger.ownerCt.caller;
		}
		if (bool == false) {
			return;
		}
		this.setFieldStyle('background:#C6E2FF;');
		var key = this.triggerName||this.name,
		// name属性
		dbfind = '',
		// 需要dbfind的表和字段
		dbBaseCondition = '',
		dbCondition = '',
		dbGridCondition = '',
		findConfig = this.findConfig,
		dbKey = this.dbKey,
		mappingKey = this.mappingKey,
		gridKey = this.gridKey,
		mappinggirdKey = this.mappinggirdKey;
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if (findConfig) {
			dbCondition = (typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (dbKey) {
			var dbKeyValue ='';
			if(Ext.getCmp(dbKey)){
				dbKeyValue = Ext.getCmp(dbKey).value;
			}else if(trigger.ownerCt.xtype=="myform"){//mutilform中移除了id属性，无法通过id查找
				dbKeyValue=trigger.ownerCt.down("field[name='"+dbKey+"']").value;
			}
			//var dbKeyValue = Ext.getCmp(dbKey).value;
			if (dbKeyValue) {
				dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
			} else {
				showError(this.dbMessage);
				return;
			}
		}
		if (gridKey) {
			var gridKeys = gridKey.split('|');
			var mappinggirdKeys = mappinggirdKey.split('|');
			var gridErrorMessages = this.gridErrorMessage.split('|');
			for (var i = 0; i < gridKeys.length; i++) {
				var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;
				if (i == 0) {
					if (gridkeyvalue) {
						dbGridCondition = mappinggirdKeys[i] + " IS '" + gridkeyvalue + "' ";
					} else {
						showError(gridErrorMessages[i]);
						return;
					}
				} else {
					if (gridkeyvalue) {
						dbGridCondition = dbGridCondition + " AND " + mappinggirdKeys[i] + " IS '" + gridkeyvalue + "' ";
					} else {
						showError(gridErrorMessages[i]);
						return;
					}
				}
			}
		}
		if (this.dbBaseCondition) {
			dbBaseCondition = this.dbBaseCondition;
		}
		if (!trigger.ownerCt || trigger.column) { // 如果是grid的dbfind
			var grid = Ext.ComponentQuery.query('gridpanel');
			if(!trigger.dbfind){
				if(trigger.column.dataIndex == key){//
					dbfind = trigger.column.dbfind;
				}else{
					Ext.Array.each(grid,
							function(g, index) {
						Ext.Array.each(g.columns,
								function(column) {
							if (column.dataIndex == key ) {
								dbfind = column.dbfind;
								trigger.owner = g;
							}
						});
					});
				}
			}else dbfind=trigger.dbfind;

		}
		var keyValue = this.lastValue, ob = this.dbOrderby || ''; // 当前值
		keyValue = keyValue == null ? '': keyValue;
		var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
				height = Ext.isIE ? screen.height * 0.75 : '95%';
		//针对有些特殊窗口显示较小
		width =this.winWidth ? this.winWidth:width;
		height=this.winHeight ? this.winHeight:height;
		var _config=getUrlParam('_config');
		var dbwin = new Ext.window.Window({
			id: 'dbwin',
			title: '查找',
			height: height,
			width: width,
			maximizable: true,
			buttonAlign: 'center',
			modal:true,
			constrain: true,
			layout: 'anchor',
			items: [{
				tag: 'iframe',
				frame: true,
				anchor: '100% 100%',
				layout: 'fit',
				html: '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/common/dbfind.jsp?key=' + key + "&dbfind=" + encodeURIComponent(dbfind) + "&dbGridCondition=" + encodeURIComponent(dbGridCondition) + "&dbCondition=" + encodeURIComponent(dbCondition) + "&dbBaseCondition=" + encodeURIComponent(dbBaseCondition) + "&keyValue=" + encodeURIComponent(keyValue) + "&trigger=" + trigger.id + "&caller=" + dbCaller +"&_config="+_config+"&ob=" + ob + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}],
			buttons: [{
				text: '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler: function() {
					Ext.getCmp('dbwin').close();
				}
			},
			{
				width:88,
				text: '重置条件',
				id: 'reset',
				cls: 'x-btn-gray',
				iconCls: 'x-button-icon-reset',
				hidden: true,
				handler: function() {
					var grid = Ext.getCmp('dbwin').el.dom.getElementsByTagName('iframe')[0].contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
					grid.resetCondition();
					grid.getCount();
				}
			}]
		});
		dbwin.show();
		trigger.lastTriggerId = null;
	},
	getSearchData:function(table,field,condition,configSearchCondition,dbCaller,name,me,judge,searchTpl){
		var data="";
		var con = this.getExtraCondition();
    	if(con!=''){
    		condition += " AND " + con;
    	}
		Ext.Ajax.request({
			url: basePath + 'common/getSearchData.action',
			params: {
				table: table,
				field: field,
				condition:condition,
				configSearchCondition:configSearchCondition,
				name: name,
				caller: dbCaller,
				type:judge,
				searchTpl:searchTpl
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (res.success) {
					me.searchTpl=false;
					data=res;
				}else{
					me.ifsearch=false;
				}
			}
		});
		return data;
	},
	judgeForm:function(trigger){
		trigger.lastTriggerId = trigger.id;
		var judge='form';
		if (!trigger.ownerCt) {
			if (!trigger.owner) {
				trigger.getOwner();
			}
			if (trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
				trigger.record = trigger.owner.editingPlugin.activeRecord;
			} else {
				trigger.record = trigger.owner.selModel.lastSelected;
			}
			var index = trigger.owner.store.indexOf(trigger.record);
			if (index != null) {
				judge='grid';
			} else {
				judge ='form';
			}
		}
		return judge;
	},
	getCondition: function(triggerCond) {
		var condition = [], findConfig = this.findConfig, 
			dbKey = this.dbKey, mappingKey = this.mappingKey,
			gridKey = this.gridKey, mappinggirdKey = this.mappinggirdKey;
		// 存在查询条件的字段
		if (findConfig) {
			condition.push(typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (dbKey) {
			var dbKeyValue = Ext.getCmp(dbKey).value;
			if (dbKeyValue) {
				condition.push(mappingKey + "='" + dbKeyValue + "'");
			} else {
				throw new Error(this.dbMessage);
			}
		}
		if (gridKey) {
			var gridKeys = gridKey.split('|');
			var mappinggirdKeys = mappinggirdKey.split('|');
			var gridErrorMessages = this.gridErrorMessage.split('|');
			for (var i = 0; i < gridKeys.length; i++) {
				var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;
				if (gridkeyvalue) {
					condition.push(mappinggirdKeys[i] + "='" + gridkeyvalue + "'");
				} else {
					throw new Error(gridErrorMessages[i]);
				}
			}
		}
		if (this.dbBaseCondition) {
			condition.push(this.dbBaseCondition);
		}
		
		triggerCond && (condition.push(triggerCond));
		return condition.join(" AND ");
	},
	autoDbfind: function(which, caller, field, condition) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'common/autoDbfind.action',
			params: {
				which: which,
				caller: caller,
				field: field,
				condition: condition,
				_config:getUrlParam('_config')
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (res.data) {
					var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					me.dbfinds= res.dbfinds || me.owner.dbfinds;
					me.autoSetValue(data[0], res.dbfinds || me.owner.dbfinds);
				} else {
					if (me.autoShowTriggerWin)
					me.onTriggerClick();
				}
			}
		});
	},
	setSelectValue: function(which, caller, field, condition) {
		var me = this;
		Ext.Ajax.request({
			url: basePath + 'common/autoDbfind.action',
			params: {
				which: which,
				caller: caller,
				field: field,
				condition: condition,
				_config:getUrlParam('_config')
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				if (res.data) {
					var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					me.dbfinds= res.dbfinds || me.owner.dbfinds;
					me.autoSetValue(data[0], res.dbfinds || me.owner.dbfinds);
				} else {
					if (me.autoShowTriggerWin)
					me.onTriggerClick();
				}
			}
		});
	},
	autoSetValue: function(data, dbfinds) {
		var trigger = this;
		var triggerV = null;
		if (!trigger.ownerCt || trigger.column) { //如果是grid的dbfind
			var grid = trigger.owner;
			var record = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] || grid.selModel.lastSelected; //detailgrid里面selected
			Ext.Array.each(Ext.Object.getKeys(data),
					function(k) {
				Ext.Array.each(dbfinds,
						function(ds) {
					if (ds.trigger == trigger.name || Ext.isEmpty(ds.trigger)) {
						if (Ext.Array.contains(ds.dbGridField.split(';'), k)) { //k == ds.dbGridField//支持多dbgrid的字段对应grid同一字段
							if (ds.field == trigger.name) {
								triggerV = data[k]; //trigger所在位置赋值
							}
							record.set(ds.field, data[k]);
						}
					}
				});
			});
		} else {
			var ff;
			Ext.Array.each(Ext.Object.getKeys(data),
					function(k) {
				Ext.Array.each(dbfinds,
						function(ds) {
					if (k == ds.dbGridField) {
							if (trigger.name == ds.field||trigger.name.substring(0,trigger.name.indexOf('-new'))==ds.field) {
								triggerV = data[k];
							} else {
								ff = Ext.getCmp(ds.field);
								if (ff && ff.setValue) ff.setValue(data[k]);
							}
					}
				});
			});
		}
		trigger.setValue(triggerV);
		data.data = data;
		trigger.lastTriggerValue='';
		trigger.fireEvent('aftertrigger', trigger, data);
		trigger.lastTriggerId = null;
		trigger.lastQueryValue='';
	},
	getValue: function() {
        var me = this,
            val = me.rawToValue(me.processRawValue(me.getRawValue()));
        me.value = val;
        return val;
    },
	resetDbfindValue:function(dbfinds){
		var trigger = this;
		var triggerV = null;
		if (!trigger.ownerCt) { 
			var grid = trigger.owner;
			var record = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] || grid.selModel.lastSelected; //detailgrid里面selected
			Ext.Array.each(dbfinds,function(ds) {
				if(ds.trigger==trigger.name){
					record.set(ds.field, null);
				}				
			});
		} else {
			var ff;
			Ext.Array.each(dbfinds,function(ds) {
				ff=Ext.getCmp(ds.field);
				if(ff) ff.setValue(null);
			});
		}
		trigger.setValue(null);
		trigger.fireEvent('aftertrigger', trigger, null);
		trigger.lastTriggerId = null;
	},
    getExtraCondition: function(){ //从表放大镜额外条件
    	var me = this;
    	var condition = [];	
    	if((!me.ownerCt || me.column) && me.column.dbfind){//如果是grid的dbfind
    		var dbfind = me.column.dbfind;
    		var cond = dbfind.split('|')[2];
    		if(cond){
    			var first = cond.split('&');
    			if(first){
    				for(var i = 0;i<first.length;i++){
	    				var second = first[i].split('IS');
	    				var other=second[1].split('}');
	    				if(second[1].split(':')[0].replace('{','')=='MAIN'){	    					
	    					var formfield = other[0].split(':')[1];
	    					var field = Ext.getCmp(''+formfield+'');
	    					if(field!=null && field.value!=''){
	    						if(other[1]){
	    							condition.push(second[0] +"='"+field.value+"'"+other[1]);
		    					}else{
		    						condition.push(second[0] +"='"+field.value+"'");
		    					}
	    					}
	    					
	    				}else if(second[1].split(':')[0].replace('{','')=='DETAIL'){
	    					var gridfield = other[0].split(':')[1];
	    					var value='';
	    					if(me.record && me.record.data){
	    						value = me.record.data[gridfield];
	    					} 
	    					if(value != null && value!=''){
	    						if(other[1]){
	    							condition.push(second[0] +"='"+value+"'"+other[1]);
		    					}else{
		    						condition.push(second[0] +"='"+value+"'");
		    					}
	    					}
	    				}
    				}
    			}
    		}
    	}
    	return condition.join(' AND ');
    }
});
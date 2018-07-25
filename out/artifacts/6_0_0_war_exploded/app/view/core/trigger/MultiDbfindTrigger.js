/**
 *  multi dbfind trigger
 * 
 */
Ext.define('erp.view.core.trigger.MultiDbfindTrigger', {
	extend: 'Ext.form.ComboBox',
	alias: 'widget.multidbfindtrigger',
	triggerCls: 'x-form-search-trigger',
	separator : '#',
	_f:0,
	//支持联想
	autoDbfind: true,
	autoShowTriggerWin:true,
	searchField:'',
	searchTable:'',
	searchFieldArray:'',
	configSearchCondition:'1=1',
	ignoreMonitorTab:false,
	lastTriggerValue:null,
	ifsearch:true,
	searchTpl:true,
	canblur : true,
	lastQueryValue:'',
    minChars:1, // 设置用户输入字符多少时触发查询
    tpl: '',
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
        me.callParent(arguments);
        if(me.clearable) {
        	me.trigger2Cls = 'x-form-clear-trigger';
        	if(!me.onTrigger2Click) {
        		me.onTrigger2Click = function(){
        			this.setValue(null);
        		};
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
				var con = !Ext.isEmpty(this.lastValue) ? (key + " like '%" + this.lastValue.replace(/\'/g,"''")  + "%'") : null;
				var currrecord = null;
				if (!this.ownerCt || this.column) {
					which = 'grid';
					dbfind = this.column.dbfind;
					cal = dbfind.split('|')[0];
					con = con ? (dbfind.split('|')[1] + " like '%" + this.lastValue.replace(/\'/g,"''")  + "%'") : null;
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
		var trigger = this,bool = true; // 放大镜所在;
		this.setFieldStyle('background:#C6E2FF;');
		bool = this.fireEvent('beforetrigger', trigger);
		if (bool == false) {
			return;
		}
		var key = this.name,// name属性
			dbfind = '',// 需要dbfind的表和字段
			dbBaseCondition = '',
			dbCondition = '',
			dbGridCondition = '',
			dbKey = this.dbKey,
			mappingKey = this.mappingKey,
			mappinggirdKey = this.mappinggirdKey,
			gridKey = this.gridKey,
			gridErrorMessage = this.gridErrorMessage;
		
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if(dbKey){
			var dbKeyValue = Ext.getCmp(dbKey).value;
			if(dbKeyValue){
				dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
			} else {
				showError(this.dbMessage);
				return;
			}
		}
		if(gridKey){
			var gridKeys = gridKey.split('|');
			var mappinggirdKeys;
			var gridErrorMessages;
			if(mappinggirdKey){
				mappinggirdKeys = mappinggirdKey.split('|');
				gridErrorMessages = this.gridErrorMessage.split('|');
			}
			
			for(var i=0;i<gridKeys.length;i++){
				var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;
				
				if(i==0){
					if(gridkeyvalue){
						dbGridCondition = mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
					}else{
						showError(gridErrorMessages[i]);
				    	return;
					}
				}else{
					if(gridkeyvalue){
						dbGridCondition =dbGridCondition+" AND "+ mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
					}else{
						showError(gridErrorMessages[i]);
				    	return;
					}
				}
			}
		}
		if(this.dbBaseCondition){
			dbBaseCondition = this.dbBaseCondition;
		}
		if(!trigger.ownerCt|| trigger.column){// 如果是grid的dbfind
			var grid = Ext.ComponentQuery.query('gridpanel');
			Ext.Array.each(grid, function(g, index){
				Ext.Array.each(g.columns,function(column){
					if(column.dataIndex == key) {
						dbfind = column.dbfind;
						trigger.owner = g;
					}
				});
			});
			if(trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
				trigger.record = trigger.owner.editingPlugin.activeRecord;
			} else {
				trigger.record = trigger.owner.selModel.lastSelected;
			}

			var con = this.getExtraCondition();//前台界面条件
			if(con!=''){
				if(dbBaseCondition!=''){
					dbBaseCondition += " AND " + con;
		    	}else{
		    		dbBaseCondition +=  con;
		    	}
			} 
		}else  caller=caller||trigger.ownerCt.caller;

		var keyValue = this.value;// 当前值
		keyValue = keyValue == null ? '' : keyValue;
		var _config=getUrlParam('_config');
		var dbwin = this.createWin();
		dbwin.show();
		if(this.multistore){
			this.showButtons();
		} else {
			trigger.multiValue = new Object();
			var iframe = dbwin.getEl().down('iframe');
			if(!iframe) {
				dbwin.add({
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe src="#" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				});
				iframe = dbwin.getEl().down('iframe');
			}
			iframe.dom.src = basePath + 'jsps/common/multidbfind.jsp?key=' + 
				key + "&dbfind=" + encodeURIComponent(dbfind) + 
				"&dbGridCondition=" + encodeURIComponent(dbGridCondition) + "&dbCondition=" + encodeURIComponent(dbCondition) + 
				"&dbBaseCondition=" + encodeURIComponent(dbBaseCondition) + "&keyValue=&caller=" + caller +"&_config="+_config+
				"&trigger=" + trigger.id+"&_f="+ trigger._f;
		}
	},
	createWin: function() {
		var trigger = this;
		this.win = Ext.create('Ext.Window', {
			title: '查找',
			height: "100%",
			width: "80%",
			maximizable : true,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [],
			dbtriggr: trigger,
			closeAction: 'hide',
			buttons : [{
			 xtype:'panel',
			 width:150,
			 height:25,
			 border:false,
			 bodyStyle:'background-color:#e8e8e8',
			 items:[{
			  boxLabel  : '<span style="font-size:13px;font-weight:bold;">只显示已选中数据</span>',
				xtype:'checkbox',
				style:'margin-left:10px;',
				align:'left',
				hidden:true,
				width:140,
				id:'onlyChecked',
				listeners:{
					change:function( f, newValue,  oldValue, eOpts ){
						var win = trigger.win;
						var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
						var resgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindresultgrid');//选中
						if(newValue){
							findgrid.hide();
							var datachecked=new Array();
							Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
								datachecked.push(resgrid.selectObject[k]);
							});
							resgrid.selectAll=false;
							resgrid.store.loadData(datachecked);
							resgrid.selModel.selectAll();
							resgrid.show();
						}else{
							findgrid.show();
							findgrid.selectAll=false;
							findgrid.selModel.deselectAll();
							findgrid.selectDefaultRecord();
						    resgrid.hide();
						}
					}
				}
			 }]
			},'->',{
				text : '确  认',
				iconCls: 'x-button-icon-confirm',
				id:'mutidbaffirm',
				cls: 'x-btn-gray',
				handler : function(){
					trigger.onConfirm();
				}
			},{
				text : '按条件全选',
				iconCls: 'x-button-icon-confirm',
				cls: 'x-btn-gray',
				id:'mutidbcondition',
				handler : function(){
					trigger.onConfirm(true);
				}
			},{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				style:'margin-right:140px',
				handler : function(btn){
					btn.ownerCt.ownerCt.close();
				}
			} ,'->'
			]
		});
		return this.win;
	},
	showButtons: function() {
		var trigger = this;
		var value = this.value, dbwin = this.win, grid = dbwin.down('gridpanel');
		if (grid) {
			grid.store.loadData(this.multistore);
		} else {
			grid = dbwin.add({
				xtype: 'gridpanel',
				height: '100%',
				autoScroll: true,
				columnLines : true,
				columns: [{ 
					text: '描述',  
					dataIndex: 'display' ,
					flex: 1,
					filter: {
						xtype: 'textfield'
					}
				}, { 
					text: '代码', 
					dataIndex: 'value', 
					flex: 1,
					filter: {
						xtype: 'textfield'
					}
				}],
				store: this.multistore,
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
					ignoreCase: true
				})],
				selModel: Ext.create('Ext.selection.CheckboxModel',{
					ignoreRightMouseSelection : false,
					listeners:{
						selectionchange:function(selectionModel, selected, options){
							if(selected.length==0&&selectionModel.store.data.length>0){//取消全选
								Ext.each(selectionModel.store.data.items,function(item){
									Ext.Array.remove(grid.multiselected, item.data.value);
								});
							}else{
								Ext.each(selected,function(s){
									grid.multiselected.push(s.data.value);
								});
							}
							grid.multiselected=Ext.Array.unique(grid.multiselected);    	
						}
					},
					onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
						view.el.focus();
						var me = view.ownerCt, val = record.get('value');
						var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
						// 2018070027 zhuth 2018-7-3 修改按钮包含判断规则，添加前缀并区分大小写
						if(contains(value, trigger.separator + val, false) || value.startsWith(val)){
							me.selModel.deselect(record);
							Ext.Array.remove(me.multiselected, val);
							checkbox.setAttribute('class','x-grid-row-checker');
						} else {
							if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
								checkbox.setAttribute('class','x-grid-row-checker-checked');//只是修改了其样式，并没有将record加到selModel里面
								me.multiselected.push(val);
							} else {
								Ext.Array.remove(me.multiselected, val);
								checkbox.setAttribute('class','x-grid-row-checker');
							}
						}
					}
				})
			});
			grid.store.on('datachanged', function(){
				var selected = new Array();
				grid.store.each(function(){
					if(Ext.Array.contains(grid.multiselected, this.get('value'))){
						selected.push(this);
					}
				});
				grid.selModel.select(selected);
			});
		}
		var selected = new Array();
		grid.multiselected = Ext.isEmpty(value) ? [] : value.split(trigger.separator);
		grid.store.each(function(){
			// 2018070027 zhuth 2018-7-3 修改按钮包含判断规则，添加前缀并区分大小写
			var buttonValue = this.data['value'];
			if(contains(value, trigger.separator + buttonValue, false) || value.startsWith(buttonValue)){
				selected.push(this);
			}
		});
		grid.selModel.select(selected);
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
	onConfirm: function(selectAll) {
		var trigger = this;
		if(trigger.multistore){
			var grid = this.win.down('gridpanel');
			if(selectAll&&grid.store.data.length>0){//按条件全选
				Ext.each(grid.store.data.items,function(item){
					grid.multiselected.push(item.data.value);
				});
			}
			grid.multiselected=Ext.Array.unique(grid.multiselected);    
			trigger.setValue(grid.multiselected.join(trigger.separator));
			this.win.close();
		} else {
			if(!trigger.ownerCt|| trigger.column){
				if (!trigger.owner) {
					trigger.getOwner();
				}
				var grid = trigger.owner;
				var record = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] || grid.selModel.lastSelected;//detailgrid里面selected
				if(selectAll) {
					trigger.getAllData(trigger, grid, record);
				} else {
					var win = trigger.win;
					var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
					findgrid.setMultiValues();
					Ext.each(trigger.multiValue, function(item, index){
						if(index > 0){
							record = trigger.next(grid, record);
						}
						if(record) {
							if(item){
								Ext.Array.each(Ext.Object.getKeys(item), function(k){
									Ext.Array.each(grid.dbfinds,function(ds){
										if(Ext.isEmpty(ds.trigger) || ds.trigger == trigger.name) {
											if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {
												record.set(ds.field, item[k]);
											}
										}
									});
								});
							}
						}
					});
				}
			} else {
				var win = trigger.win;
					var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
					findgrid.setMultiValues();
					
				var k = Ext.Object.getKeys(trigger.multiValue),cp;
				Ext.each(k, function(key){
					cp = Ext.getCmp(key);
					if(cp.setValue !== undefined)
						cp.setValue(trigger.multiValue[key]);
				});
				trigger.setValue(trigger.multiValue[trigger.name]);
			}
			if(!selectAll) {
				trigger.fireEvent('aftertrigger', trigger, trigger.multiRecords);
				this.win.close();
			}
		}
	},
	/**
	 * 全选模式下，取全部满足条件的数据
	 */
	getAllData: function(trigger, grid, record) {
		var win = this.win, g = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');
		g.setLoading(true);
		g.getAllData(function(datas){
			if(datas) {
				Ext.each(datas, function(item, index){
					if(index > 0){
						record = trigger.next(grid, record);
					}
					if(record) {
						Ext.Array.each(Ext.Object.getKeys(item), function(k){
							Ext.Array.each(grid.dbfinds, function(ds){
								if(Ext.isEmpty(ds.trigger) || ds.trigger == trigger.name) {
									if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {
										record.set(ds.field, item[k]);
									}
								}
							});
						});
					}
				});
			}
			g.setLoading(false);
			trigger.fireEvent('aftertrigger', trigger, datas);
			win.close();
		});
	},
	/**
	 * 递归grid的下一条 
	 */
	next: function(grid, record){
		record = record || grid.selModel.lastSelected;
		if(record){
			//递归查找下一条，并取到数据
			var store = grid.store, idx = store.indexOf(record),
				d = store.getAt(idx + 1), len = store.data.items.length;
			if(d){
				return d;
			} else {
				if(idx + 1 < len){
					this.next(grid, d);
				} else {
					if (grid.GridUtil) {
						grid.GridUtil.add10EmptyItems(grid);
						return this.next(grid, record);
					}
				}
			}
		}
	},
	getExtraCondition: function(){ //从表放大镜额外条件
    	var me = this,key = this.name;// name属性;
    	var condition = [];	
    	if(!me.ownerCt){//如果是grid的dbfind
    		var grid = Ext.ComponentQuery.query('gridpanel');
			Ext.Array.each(grid, function(g, index){
				Ext.Array.each(g.columns,function(column){
					if(column.dataIndex == key) {
						dbfind = column.dbfind;
						me.owner = g;
					}
				});
			});
			if(dbfind){
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
		    					var value = me.record.data[gridfield];
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
		}	
    	return condition.join(' AND ');
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
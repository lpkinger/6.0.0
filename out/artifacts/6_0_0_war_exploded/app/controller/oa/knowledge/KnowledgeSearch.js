Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeSearch', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    BaseUtil:Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.knowledge.Recknowledge','common.datalist.Toolbar',
             'oa.knowledge.KnowledgeSearchForm','oa.knowledge.KnowledgeSearchGrid','core.trigger.DbfindTrigger'
    	],
    init:function(){
    var grid=Ext.getCmp('grid');
    var me=this;
    	this.control({ 
    	'erpSearchGridPanel': { 
    			//itemclick: me.onGridItemClick 
    		  },
    		'textfield[id=search]':{
    		  change:function(){
    		   currentnum=1;
    		    me.onTextFieldChange(me);
    		  }
    		},
    		'checkbox[id=regular]':{
    		  change:function(){
    		   me.regExpToggle(me);
    		  }   		
    		},
    		'checkbox[id=case]':{
    		  change:function(){
    		  me.caseSensitiveToggle(me);
    		  }   		
    		},
    		'button[id=prev]':{
    		  click:function(){
    		   me.onPreviousClick();
    		  }
    		},	
    		'button[id=next]':{
    		  click:function(){
    		   me.onNextClick();
    		  }
    		},
    		'button[id=previous]':{
    		  click:function(){
    		   me.previous(me);
    		  }
    		},
    		'button[id=nextone]':{
    		  click:function(){
    		   me.next(me);
    		  }   		
    		}
    	});
    },
       onTextFieldChange: function(base) {
         var me = Ext.getCmp('grid'),
             count = 0;
         me.view.refresh();
         me.searchValue = me.getSearchValue();
         me.indexes = [];
         me.currentIndex = null;
         if (me.searchValue !== null) {
             me.searchRegExp = new RegExp(me.searchValue, 'g' + (me.caseSensitive ? '' : 'i')); 
             me.store.each(function(record, idx) {
                 var td = Ext.fly(me.view.getNode(idx)).down('td'),
                     cell, matches, cellHTML;
                 while(td) {
                     cell = td.down('.x-grid-cell-inner');
                     matches = cell.dom.innerHTML.match(me.tagsRe);
                     cellHTML = cell.dom.innerHTML.replace(me.tagsRe, me.tagsProtect);
                     
                     // populate indexes array, set currentIndex, and replace wrap matched string in a span
                     cellHTML = cellHTML.replace(me.searchRegExp, function(m) {
                        count += 1;
                        if (Ext.Array.indexOf(me.indexes, idx) === -1) {
                            me.indexes.push(idx);
                        }
                        if (me.currentIndex === null) {
                            me.currentIndex = idx;
                        }
                        if(count==currentnum){
                           return '<span class=" x-livesearch-matchbase">' + m + '</span>';
                        }
                        else  return '<span class="' + me.matchCls + '">' + m + '</span>';
                     });
                     
                     // restore protected tags
                     Ext.each(matches, function(match) {
                        cellHTML = cellHTML.replace(me.tagsProtect, match); 
                     });
                     // update cell html
                     cell.dom.innerHTML = cellHTML;
                     td = td.next();
                 }
             }, me);
             if (me.currentIndex !== null) {
                 me.getSelectionModel().select(me.currentIndex);
             }
         }

         // no results found
         if (me.currentIndex === null) {
             me.getSelectionModel().deselectAll();
         }
         
        matchcount=count;
        if(count>2){
         Ext.getCmp('previous').setDisabled(false);
         Ext.getCmp('nextone').setDisabled(false);
        }else{
         Ext.getCmp('previous').setDisabled(true);
         Ext.getCmp('nextone').setDisabled(true);
        }
        if(count!=0){
        Ext.getCmp('matchs').setValue('第  '+currentnum+' 个,共 '+count+' 个');
        }else Ext.getCmp('matchs').reset();
        Ext.getCmp('search').focus();
     },
     caseSensitiveToggle: function(me,checkbox) {
        Ext.getCmp('grid').caseSensitive = Ext.getCmp('case').checked;
         me.onTextFieldChange();
    },
    regExpToggle: function(me,checkbox, checked) {
        Ext.getCmp('grid').regExpMode = Ext.getCmp('regular').checked;
        me.onTextFieldChange();
    },
     onPreviousClick: function() {
        var me = Ext.getCmp('grid'),
            idx;
            
        if ((idx = Ext.Array.indexOf(me.indexes, me.currentIndex)) !== -1) {
            me.currentIndex = me.indexes[idx - 1] || me.indexes[me.indexes.length - 1];
            me.getSelectionModel().select(me.currentIndex);
         }
    },  
    onNextClick: function() {
          var me = Ext.getCmp('grid'),
             idx;
             
         if ((idx = Ext.Array.indexOf(me.indexes, me.currentIndex)) !== -1) {
            me.currentIndex = me.indexes[idx + 1] || me.indexes[0];
            me.getSelectionModel().select(me.currentIndex);
         }
    },
    previous:function(me){
      if(currentnum==1){
        currentnum=matchcount;    
      }else  currentnum=currentnum-1;
      me.onTextFieldChange();
    
    },
    next:function(me){
       if(currentnum==matchcount){
        currentnum=1;    
      }else currentnum=currentnum+1;
     me.onTextFieldChange();
    },
     onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
        var scanpersonid=record.data.kl_scanpersonid+'#';
        var authorid=record.data.kl_authorid;
    	if(scanpersonid&&scanpersonid.indexOf(emid)<0&&authorid!=emid){
    	  //说明没有权限查看  需提出申请
    	  var win = new Ext.window.Window(
				{
					id : 'win',
					height : '350',
					width : '550',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeApply'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
		return;
    	}
    	if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
    		var value = record.data[keyField];
        	var formCondition = keyField + "IS" + value ;
        	var gridCondition = pfField + "IS" + value;
        	var mappingCondition='kl_kindidIS'+record.data.kl_kindid+' And '+keyField+'NO'+value;
        	var panel = Ext.getCmp(caller + keyField + "=" + value); 
        	var main = parent.Ext.getCmp("content-panel");
        	if(!main){
				main = parent.parent.Ext.getCmp("content-panel");
			}
        	if(!panel){ 
        		var title = "";
    	    	if (value.toString().length>4) {
    	    		 title = value.toString().substring(value.toString().length-4);	
    	    	} else {
    	    		title = value;
    	    	}
    	    	var myurl = '';
    	    	if(me.BaseUtil.contains(url, '?', true)){
    	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition+'&mappingCondition='+mappingCondition;
    	    	} else {
    	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition+'&mappingCondition='+mappingCondition;
    	    	}
    	    	myurl += "&datalistId=" + main.getActiveTab().id;
    	    	main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页
    	    	panel = {       
    	    			title : me.BaseUtil.getActiveTab().title+'('+title+')',
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
    	    			closable : true,
    	    			listeners : {
    	    				close : function(){
    	    					if(!main){
    	    						main = parent.parent.Ext.getCmp("content-panel");
    	    					}
    	    			    	main.setActiveTab(main.getActiveTab().id); 
    	    				}
    	    			} 
    	    	};
    	    	this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
        	}else{ 
    	    	main.setActiveTab(panel); 
        	} 
    	}
    }, 
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	/*var tab = main.getComponent(o); */
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    },
    getCurrentStore: function(value){
    	var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data[keyField];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data[keyField];
			}
			var v = item.data[keyField];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
    }
});
/* @private
 * This is an internal helper class for the calendar views and should not be overridden.
 * It is responsible for the base event rendering logic underlying all views based on a 
 * box-oriented layout that supports day spanning (MonthView, MultiWeekView, DayHeaderView).
 */
Ext.define('Ext.calendar.util.WeekEventRenderer', {
    
    requires: ['Ext.core.DomHelper'],
    
    statics: {
        // private
        getEventRow: function(id, week, index) {
            var indexOffset = 1,
                evtRow,
                wkRow = Ext.get(id + '-wk-' + week);
            if (wkRow) {
                var table = wkRow.child('.ext-cal-evt-tbl', true);
                evtRow = table.tBodies[0].childNodes[index + indexOffset];
                if (!evtRow) {
                    evtRow = Ext.core.DomHelper.append(table.tBodies[0], '<tr></tr>');
                }
            }
            return Ext.get(evtRow);
        },
        render: function(o) {
        	
            var w = 0,
                grid = o.eventGrid,
                dt = Ext.Date.clone(o.viewStart),
              
                eventTpl = o.tpl,
                max = o.maxEventsPerDay != undefined ? o.maxEventsPerDay: 999,
                weekCount = o.weekCount < 1 ? 6: o.weekCount,
                dayCount = o.weekCount == 1 ? o.dayCount: 7,
                cellCfg;
            var dtadd=0;
           
            for (; w < weekCount; w++) {
            
                if (!grid[w] || grid[w].length == 0) {
                    if (weekCount == 1) {
                        row = this.getEventRow(o.id, w, 0);
                        cellCfg = {
                            tag: 'td',
                            cls: 'ext-cal-ev',
                            id: o.id + '-empty-0-day-' + Ext.Date.format(dt, 'Ymd'),
                            html: '&#160;'
                        };
                        if (dayCount > 1) {
                            cellCfg.colspan = dayCount;
                        }
                        Ext.core.DomHelper.append(row, cellCfg);
                    }
                    dt = Ext.calendar.util.Date.add(dt, {days: 7});
                } else {
                    var row,
                        d = 0,
                        wk = grid[w],
                        startOfWeek = Ext.Date.clone(dt),
                        endOfWeek = Ext.calendar.util.Date.add(startOfWeek, {days: dayCount, millis: -1});
                    
                    for (; d < dayCount; d++) {
                        if (wk[d]) {
                            var ev = emptyCells = skipped = 0,
                                day = wk[d],
                                ct = day.length,
                                evt;

                            for (; ev < ct; ev++) {
                            	
                                if (!day[ev]) {
                                    emptyCells++;
                                    continue;
                                }
                                if (emptyCells > 0 && ev - emptyCells < max) {
                                    row = this.getEventRow(o.id, w, ev - emptyCells);
                                    cellCfg = {
                                       tag: 'td',
                                        cls: 'ext-cal-ev',
                                       id: o.id + '-empty-' + ct + '-day-' + Ext.Date.format(dt, 'Ymd')
                                    };
                                    if (emptyCells > 1 && max - ev > emptyCells) {
                                        cellCfg.rowspan = Math.min(emptyCells, max - ev);
                                    }
                                    Ext.core.DomHelper.append(row, cellCfg);
                                    emptyCells = 0;
                                }

                                if (ev >= max) {
                                    skipped++;
                                    continue;
                                }
                                evt = day[ev];
                                if (!evt.isSpan || evt.isSpanStart) {
                                	
                                    //skip non-starting span cells
                                    var item = evt.data || evt.event.data;
                                    item._weekIndex = w;
                                    item._renderAsAllDay = item[Ext.calendar.data.EventMappings.IsAllDay.name] || evt.isSpanStart;
                                    item.spanLeft = item[Ext.calendar.data.EventMappings.StartDate.name].getTime() < startOfWeek.getTime();
                                    item.spanRight = item[Ext.calendar.data.EventMappings.EndDate.name].getTime() > endOfWeek.getTime();
                                    item.spanCls = (item.spanLeft ? (item.spanRight ? 'ext-cal-ev-spanboth':
                                    'ext-cal-ev-spanleft') : (item.spanRight ? 'ext-cal-ev-spanright': ''));
                                  //剔除周末 
                                    row = this.getEventRow(o.id, w, ev); 
                                    var containweeks=item.IsContainWeekends,
                                        weekends=0;     
                                        if(item.WeekEnds){
                                        	weekends=item.WeekEnds;
                                        }
                                        cellCfg = '';
                                        //不包含周末的处理 以及单双休的处理
                                  if(!containweeks){     
                                     if(dtadd>0){
                                    	dt=Ext.Date.add(new Date(dt), Ext.Date.DAY, -1);
                                    	dtadd=0;
                                    }
                                  var eventStartDate=item.StartDate;
                                  var starttime=new Date(eventStartDate);
                                  var newdif= Ext.calendar.util.Date.diffDays(starttime,dt)+1;
                                
                                   if(dt.getDay()!=0&&newdif<8){
                                	
                                        cellCfg= {
                                        tag: 'td',
                                        cls: 'ext-cal-ev',
                                        cn: eventTpl.apply(o.templateDataFn(item))
                                      };
                                   }else if(dt.getDay()!=0&&newdif>8){ 
                                	  
                                       Ext.core.DomHelper.append(row,  {
                                        tag: 'td',
                                         cls: 'ext-cal-ev',
                                         cn: {
                                         tag: 'a',
                                         html:'周末休假'
                                         }
                                         });                                        
                                         cellCfg= {
                                         tag: 'td',
                                         cls: 'ext-cal-ev',
                                         cn: eventTpl.apply(o.templateDataFn(item))
                                       };
                                	   
                                   }                           
                                   else{
                                	   
                                      Ext.core.DomHelper.append(row,  {
                                       tag: 'td',
                                        cls: 'ext-cal-ev',
                                        cn: {
                                        tag: 'a',
                                        html:'周末休假'
                                        }
                                        });
                                     
                                        dt=Ext.Date.add(new Date(dt), Ext.Date.DAY, 1);
                                        dtadd=1;
                                        cellCfg= {
                                        tag: 'td',
                                        cls: 'ext-cal-ev',
                                        cn: eventTpl.apply(o.templateDataFn(item))
                                      };
                                        
                                    }                         
                                   var diff = Ext.calendar.util.Date.diffDays(dt, item[Ext.calendar.data.EventMappings.EndDate.name]) + 1;                              
                                   var  cspan = Math.min(diff, dayCount - d);
                                   var Enddiff = Ext.calendar.util.Date.diffDays(item[Ext.calendar.data.EventMappings.EndDate.name], endOfWeek) + 1; 
                                   if(weekends>1){
                                     if(Enddiff<1){
                                     if(cspan>6){
                                       	 cspan=6;
                                        }
                                      cspan=cspan-1;
                                      if (cspan > 1) {
                                          cellCfg.colspan = cspan;
                                      }
                                      Ext.core.DomHelper.append(row, cellCfg);
                                      Ext.core.DomHelper.append(row,  {
                                          tag: 'td',
                                           cls: 'ext-cal-ev',
                                           cn: {
                                           tag: 'a',
                                           html:'周末休假'
                                           }
                                           });
                                     }
                                     else{
                                     //对于双休的处理  开始时间为周六 隐藏
                                     if(dt.getDay()==6){
                                    	 cellCfg= {
                                                 tag: 'td',
                                                 cls: 'ext-cal-ev',
                                                 cn:{
                                                	 tag:'a',
                                                	html:'周末休假'	 
                                                 }
                                    	 }
                                     }
                                     if (cspan > 1) {
                                         cellCfg.colspan = cspan;
                                     }
                                     Ext.core.DomHelper.append(row, cellCfg);
                                     }
                                   }
                                   else{
                                	   if(cspan>6){
                                         	 cspan=6;
                                          }   
                                        
                                    if (cspan > 1) {
                                        cellCfg.colspan = cspan;
                                    }
                                    Ext.core.DomHelper.append(row, cellCfg);
                                   }
                                    
                                }
                                  //包含周末的处理
                                  else {
                                	     cellCfg = {
                                              tag: 'td',
                                              cls: 'ext-cal-ev',
                                              cn: eventTpl.apply(o.templateDataFn(item))
                                          };
                                          var diff = Ext.calendar.util.Date.diffDays(dt, item[Ext.calendar.data.EventMappings.EndDate.name]) + 1,
                                              cspan = Math.min(diff, dayCount - d);
                                          if (cspan > 1) {
                                              cellCfg.colspan = cspan;
                                          }
                                          Ext.core.DomHelper.append(row, cellCfg);
                                  }
                                    
                                   
                                   
                                }
                               
                            }
                            if (ev > max) {
                              if(dt.getDay()!=0){
                                row = this.getEventRow(o.id, w, max);
                                Ext.core.DomHelper.append(row, {
                                    tag: 'td',
                                    cls: 'ext-cal-ev-more',
                                    id: 'ext-cal-ev-more-' + Ext.Date.format(dt, 'Ymd'),
                                    cn: {
                                        tag: 'a',
                                        html: '+' + skipped + ' 更多...'
                                    }
                                });
                               }else{
                                 row = this.getEventRow(o.id, w, max);
                                 Ext.core.DomHelper.append(row, {
                                    tag: 'td',
                                   cn: {
                                        tag: 'a',
                                    }
                                    
                                });
                                
                                }
                            }
                            if (ct < o.evtMaxCount[w]) {
                                row = this.getEventRow(o.id, w, ct);
                                if (row) {
                                    cellCfg = {
                                        tag: 'td',
                                        cls: 'ext-cal-ev',
                                        id: o.id + '-empty-' + (ct + 1) + '-day-' + Ext.Date.format(dt, 'Ymd')
                                    };
                                    var rowspan = o.evtMaxCount[w] - ct;
                                    if (rowspan > 1) {
                                        cellCfg.rowspan = rowspan;
                                    }
                                    Ext.core.DomHelper.append(row, cellCfg);
                                }
                            }
                            
                        } else {
                        	
                            row = this.getEventRow(o.id, w, 0);
                            if (row) {
                                cellCfg = {
                                    tag: 'td',
                                    cls: 'ext-cal-ev',
                                    id: o.id + '-empty-day-' + Ext.Date.format(dt, 'Ymd')
                                };
                                if (o.evtMaxCount[w] > 1) {
                                    cellCfg.rowSpan = o.evtMaxCount[w];
                                }
                                Ext.core.DomHelper.append(row, cellCfg);
                            }
                        }
                        dt = Ext.calendar.util.Date.add(dt, {days: 1});
                    }
                }
            }
        }
    }
});
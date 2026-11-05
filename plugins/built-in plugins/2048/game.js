let board, score, won, over, startX, startY;
const grid = document.getElementById('board');
function addTile() { const empty = board.map((v, i) => v ? -1 : i).filter(i => i >= 0); if (empty.length) board[empty[Math.floor(Math.random() * empty.length)]] = Math.random() < 0.9 ? 2 : 4; }
function draw(message) { grid.replaceChildren(); board.forEach(v => { const cell = document.createElement('div'); cell.className = 'tile' + (v ? '' : ' empty'); cell.textContent = v || ''; grid.appendChild(cell); }); document.getElementById('score').textContent = score; if (message !== undefined) document.getElementById('message').textContent = message; }
function reset() { board = Array(16).fill(0); score = 0; won = false; over = false; addTile(); addTile(); draw('Swipe to move'); }
function move(dr, dc) { if (over) return; let changed = false, reached = false;
  for (let line = 0; line < 4; line++) { const indexes = Array.from({length:4}, (_, i) => dr ? (dr < 0 ? i : 3-i)*4+line : line*4+(dc < 0 ? i : 3-i)); const old = indexes.map(i => board[i]); const values = old.filter(Boolean); const result = [];
    for (let i=0; i<values.length; i++) { if (i+1<values.length && values[i]===values[i+1]) { const v=values[i]*2; result.push(v); score+=v; i++; if (v>=2048 && !won) { won=true; reached=true; } } else result.push(values[i]); }
    while (result.length<4) result.push(0); indexes.forEach((idx,i) => { if (old[i]!==result[i]) changed=true; board[idx]=result[i]; }); }
  if (changed) addTile(); const canMove=board.some((v,i) => !v || (i%4<3 && v===board[i+1]) || (i<12 && v===board[i+4])); over=!canMove; draw(over ? 'No moves left - game over' : reached ? '2048 reached! Keep playing' : undefined);
}
grid.addEventListener('touchstart', e => { startX=e.touches[0].clientX; startY=e.touches[0].clientY; }, {passive:true});
grid.addEventListener('touchend', e => { const dx=e.changedTouches[0].clientX-startX, dy=e.changedTouches[0].clientY-startY; if (Math.max(Math.abs(dx),Math.abs(dy))<20) return; if (Math.abs(dx)>Math.abs(dy)) move(0,dx<0?-1:1); else move(dy<0?-1:1,0); });
document.getElementById('new').addEventListener('click',reset); reset();

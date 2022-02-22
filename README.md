# GameCMS Minecraft Plugin
Това е официалният плъгин за играта Minecraft за нашата платфрома

<h4>Команди към плъгина:</h4>

<ol>
  <li><code>gcms reload</code> - Plugin reload</li>
  <li><code>gcms force</code> - Изпълняване на всички направени поръчки, преди <b>payments-scheduler</b> времето което сте въвели.</li>
  <li><code>gcms setkey <key></code> - Задаване на server-key.</li>
  <li><code>gcms settime</code> - Промяна на времето за проверка в тикове. Като не може да бъде по-малко от 1200.</li>
</ol>
<hr>
<h4>Как да инсталираме правилно WStore плъгина.</h4>

<ol>
  <li>Генерирате <b>Server Key</b> от <a href="https://panel.gamecms.org/servers/" target="_blank" rel="noopener">Управление &gt; Списък със сървъри</a></li>
  <li>Слагате плъгина в папката plugins на вашият сървър.</li>
  <li>Стартирате/рестартирате сървъра.</li>
  <li>Отваряте папката plugins/GameCMS/config.yml</li>
  <li>След като отворите config.yml файла. На първият ред ще намерите <code> server-key: api_key</code> като вие трябва, да замените <code>api_key</code> с <b>Server Key</b> който сте генерирали.</li>
  <li><code>payments-scheduler: 1200</code> &gt; Означава , че плъгина ще проверява за нови покупки на всяка една минута. Ако искате да промените <code>payments-scheduler: 1200</code> вие трябва да изпозлвате Minecraft Ticks като 1200 означава <b>една минута</b></li>
  <li><code>broadcast-payment: true</code> &gt; Показване на съобщението при покупка. Ако искате , да го спрете задайте тази опция на <code>false</code>. Това съобщение се променя от <a href="settings/settings.php" target="_blank" rel="noopener">Настройки &gt; Основни настройки</a></li>
  <li>Релоадванте плъгина чрез командата <code>/ws reload </code></li>
</ol>

<hr>
Препоръчително е да използвате един ключ на сървъра само за един сървър. Ако искате да използвате приставката на множество сървъри, препоръчваме ви да генерирате нов отделен ключ за всеки сървър. Ако имате допълнителни въпроси, моля свържете се с нас.

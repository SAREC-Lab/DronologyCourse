import util
import time
import argparse
import subprocess

_LOG = util.get_logger()

_CLAS = {
    'hobby1': 'missions.hobby.Neighborhoods -N 9 -n 2 -ap /home/vierhauser/.ardupilot/ardupilot',
    'hobby2': 'missions.hobby.Neighborhoods -N 20 -n 2 -ap /home/vierhauser/.ardupilot/ardupilot',
    'hobby3': 'missions.hobby.Neighborhoods -N 20 -n 2 -ap /home/vierhauser/.ardupilot/ardupilot',
    'sar': 'missions.sar.SaR -ap /home/vierhauser/.ardupilot/ardupilot',
    'news2': 'missions.news.NewsStations -n 3 -ap /home/vierhauser/.ardupilot/ardupilot',
    'news3': 'missions.news.NewsStations -n 6 -ap /home/vierhauser/.ardupilot/ardupilot'
}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('-d', '--duration',
                        type=int, default=30)
    parser.add_argument('-c', '--cooldown',
                        type=int, default=10)
    parser.add_argument('-n', '--n_reps',
                        type=int, default=1)
    parser.add_argument('-m', '--missions',
                        type=str, nargs='+', choices=tuple(list(_CLAS.keys())))

    args = parser.parse_args()
    for m_id in args.missions:
        for i in range(args.n_reps):
            _LOG.info('Starting run {} with args: -m "{}"'.format(i + 1, _CLAS[m_id]))
            p = subprocess.Popen(['python', 'main.py', '-m', _CLAS[m_id]])
            time.sleep(args.duration * 60)
            p.terminate()
            time.sleep(args.cooldown * 60)


if __name__ == '__main__':
    main()
